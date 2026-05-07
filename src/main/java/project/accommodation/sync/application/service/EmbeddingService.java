package project.accommodation.sync.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.adapter.out.persistence.model.AmenityDataRow;
import project.accommodation.sync.application.model.AccommodationEmbeddingDocument;
import project.accommodation.sync.application.model.AccommodationEmbeddingRow;
import project.accommodation.sync.application.out.command.SaveAccommodationEmbeddingPort;
import project.accommodation.sync.application.out.command.UpdateAccommodationEmbeddingStatusPort;
import project.accommodation.sync.application.out.query.LoadAccommodationEmbeddingDataPort;
import project.common.domain.DayType;
import project.common.domain.Season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmbeddingService {

    private final SaveAccommodationEmbeddingPort saveAccommodationEmbeddingPort;
    private final LoadAccommodationEmbeddingDataPort loadAccommodationEmbeddingDataPort;
    private final UpdateAccommodationEmbeddingStatusPort updateAccommodationEmbeddingStatusPort;

    //TODO : 사진 텍스트 설명 추출
    public void embedAccommodations(Pageable pageable) {
        List<Long> ids = loadAccommodationEmbeddingDataPort.getEmbeddingTargetIds(pageable);
        if (ids.isEmpty()) {
            return;
        }

        List<AccommodationEmbeddingRow> embeddingTargetRows = loadAccommodationEmbeddingDataPort.getEmbeddingRows(ids);

        Map<Long, AccommodationEmbeddingRow> baseInfoMapping = collectBaseInfo(embeddingTargetRows);
        Map<Long, Map<Season, Map<DayType, Integer>>> priceInfo = collectMetadataPrices(embeddingTargetRows);

        List<AmenityDataRow> amenityRows = loadAccommodationEmbeddingDataPort.getAmenityRows(ids);
        Map<Long, List<String>> amenitiesMapping = collectEmbedAmenities(amenityRows);

        List<AccommodationEmbeddingDocument> documents = new ArrayList<>();
        List<Long> successIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();

        for (Long id : ids) {
            try {
                AccommodationEmbeddingRow row = baseInfoMapping.get(id);
                if (row == null) continue;

                documents.add(createDocument(
                        id,
                        row,
                        priceInfo,
                        amenitiesMapping.getOrDefault(id, List.of())
                ));
                successIds.add(id);
            } catch (Exception e) {
                failedIds.add(id);
            }
        }

        try {
            saveAccommodationEmbeddingPort.saveAll(documents);
        } catch (Exception e) {
            failedIds.addAll(successIds);
            successIds.clear();
            log.error("오류 발생! 임베딩 전체 실패", e);
        }

        afterProcess(successIds, "숙소 정보 임베딩 성공", true);
        afterProcess(failedIds, "숙소 정보 임베딩 실패", false);
    }

    private Map<Long, AccommodationEmbeddingRow> collectBaseInfo(List<AccommodationEmbeddingRow> embeddingRows) {
        return embeddingRows.stream()
                            .collect(groupingBy(
                                    AccommodationEmbeddingRow::getAccommodationId,
                                    collectingAndThen(toList(), List::getFirst)
                            ));
    }

    private Map<Long, Map<Season, Map<DayType, Integer>>> collectMetadataPrices(List<AccommodationEmbeddingRow> embeddingRows) {
        return embeddingRows.stream()
                            .collect(groupingBy(
                                    AccommodationEmbeddingRow::getAccommodationId,
                                    groupingBy(
                                            AccommodationEmbeddingRow::getSeason,
                                            toMap(
                                                    AccommodationEmbeddingRow::getDayType,
                                                    AccommodationEmbeddingRow::getPrice
                                            )
                                    )
                            ));
    }

    private Map<Long, List<String>> collectEmbedAmenities(List<AmenityDataRow> amenityRows) {
        return amenityRows.stream()
                          .collect(groupingBy(
                                  AmenityDataRow::getAccommodationId,
                                  mapping(
                                          AmenityDataRow::getDescription,
                                          toList())
                          ));
    }

    private AccommodationEmbeddingDocument createDocument(
            Long id,
            AccommodationEmbeddingRow row,
            Map<Long, Map<Season, Map<DayType, Integer>>> priceInfo,
            List<String> amenities
    ) {
        Map<String, Object> metadata = getMetadata(id, priceInfo, row);
        String content = getContent(row, summarizePriceRange(metadata), amenities);

        return new AccommodationEmbeddingDocument(content, metadata);
    }

    private Map<String, Object> getMetadata(
            Long id,
            Map<Long, Map<Season, Map<DayType, Integer>>> priceInfo,
            AccommodationEmbeddingRow row
    ) {

        Map<Season, Map<DayType, Integer>> pricesMap = priceInfo.get(id);

        List<Integer> allPrices = pricesMap.values()
                                           .stream()
                                           .flatMap(dayMap -> dayMap.values().stream())
                                           .toList();

        Integer minPrice = allPrices.stream().min(Integer::compareTo).orElse(0);
        Integer maxPrice = allPrices.stream().max(Integer::compareTo).orElse(0);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("accId", id);
        metadata.put("title", row.getTitle());
        metadata.put("maxPeople", getMaxPeople(row));
        metadata.put("address", row.getAddress());
        metadata.put("minPrice", minPrice);
        metadata.put("maxPrice", maxPrice);

        return metadata;
    }

    private String getContent(
            AccommodationEmbeddingRow row,
            String priceRange,
            List<String> amenities
    ) {
        return String.format("""
                        %s은(는) %s에 위치한 숙소로, %s에서 숙소를 찾는 사용자에게 추천할 수 있는 숙소입니다.
                        %s
                        최대 %d명까지 숙박 가능하며,
                        가격대는 %s 수준입니다.
                        주요 편의시설로는 %s 등이 있습니다.
                        """,
                row.getTitle(),
                row.getAreaName() + " " + row.getSigunguName(),
                row.getSigunguName(),
                row.getDescription(),
                getMaxPeople(row),
                priceRange,
                amenities.isEmpty() ? "별도 정보 없음" : String.join(", ", amenities)
        );
    }

    private Integer getMaxPeople(AccommodationEmbeddingRow row) {
        return row.getMaxPeople() == null ? 0 : row.getMaxPeople().value();
    }

    private String summarizePriceRange(Map<String, Object> metadata) {
        int min = Integer.parseInt(metadata.get("minPrice").toString());
        int max = Integer.parseInt(metadata.get("maxPrice").toString());

        if (min == 0 || max == 0) {
            return "가격 정보 없음";
        }

        if (min == max) {
            return String.format("%,d원", min);
        }

        return String.format("%,d원 ~ %,d원", min, max);
    }

    private void afterProcess(List<Long> ids, String message, boolean embedded) {
        if (!ids.isEmpty()) {
            log.info("{} : {}", message, ids);
            updateAccommodationEmbeddingStatusPort.updateEmbeddingStatus(ids, embedded);
        }
    }
}
