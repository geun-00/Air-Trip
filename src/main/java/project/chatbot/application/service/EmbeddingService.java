package project.chatbot.application.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.chatbot.adapter.out.ai.model.AccommodationEmbeddingRow;
import project.chatbot.adapter.out.ai.model.AmenityRow;
import project.common.domain.DayType;
import project.common.domain.Season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmbeddingService {

    private final EntityManager em;
    private final VectorStore vectorStore;

    //TODO : 사진 텍스트 설명 추출
    public void embedAccommodations(Pageable pageable) {
        List<Long> ids = getEmbeddingTargetIds(pageable);

        List<AccommodationEmbeddingRow> embeddingRows = getEmbeddingRows(ids);
        Map<Long, AccommodationEmbeddingRow> baseInfoMapping = collectBaseInfo(embeddingRows);

        Map<Long, Map<Season, Map<DayType, Integer>>> priceInfo = collectMetadataPrices(embeddingRows);

        List<AmenityRow> amenityRows = getAmenityRows(ids);

        Map<Long, List<String>> amenitiesMapping = collectEmbedAmenities(amenityRows);

        List<Document> documents = new ArrayList<>();
        List<Long> successIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();

        for (Long id : ids) {
            try {
                AccommodationEmbeddingRow row = baseInfoMapping.get(id);
                if (row == null) continue;

                List<String> amenities = amenitiesMapping.getOrDefault(id, List.of());

                Map<String, Object> metadata = getMetadata(id, priceInfo, row);
                String priceRange = summarizePriceRange(metadata);

                String content = String.format("""
                                %s은(는) %s에 위치한 숙소로, %s에서 숙소를 찾는 사용자에게 추천할 수 있는 숙소입니다.
                                %s
                                최대 %d명까지 숙박 가능하며,
                                가격대는 %s 수준입니다.
                                주요 편의시설로는 %s 등이 있습니다.
                                """,
                        row.title(),
                        row.getRegion(),
                        row.sigunguName(),
                        row.description(),
                        row.maxPeople(),
                        priceRange,
                        amenities.isEmpty() ? "별도 정보 없음" : String.join(", ", amenities)
                );

                documents.add(Document.builder().text(content).metadata(metadata).build());
                successIds.add(id);
            } catch (Exception e) {
                failedIds.add(id);
            }
        }

        try {
            vectorStore.add(documents);
        } catch (Exception e) {
            failedIds.addAll(successIds);
            successIds.clear();
            log.error("오류 발생! 임베딩 전체 실패", e);
        }

        afterProcess(successIds, "숙소 정보 임베딩 성공", true);
        afterProcess(failedIds, "숙소 정보 임베딩 실패", false);
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

    private List<Long> getEmbeddingTargetIds(Pageable pageable) {
        return em.createQuery("""
                         SELECT acc.id
                         FROM Accommodation AS acc
                         WHERE acc.isEmbedded = false OR acc.isEmbedded IS NULL
                         ORDER BY acc.id
                         """, Long.class)
                 .setFirstResult((int) pageable.getOffset())
                 .setMaxResults(pageable.getPageSize())
                 .getResultList();
    }

    private List<AccommodationEmbeddingRow> getEmbeddingRows(List<Long> ids) {
        return em.createQuery("""
                         SELECT new project.chatbot.adapter.out.ai.model.AccommodationEmbeddingRow(
                             acc.id,
                             acc.title,
                             acc.detail.description,
                             acc.detail.maxPeople,
                             acc.address,
                             parentArea.codeName,
                             childArea.codeName,
                             p.season,
                             p.dayType,
                             p.price
                         )
                         FROM Accommodation AS acc
                         JOIN AccommodationPrice AS p ON p.accommodation = acc
                         JOIN AreaCode AS childArea ON childArea.code = acc.areaCode
                         LEFT JOIN AreaCode AS parentArea ON parentArea = childArea.parent
                         WHERE acc.id IN :ids
                         """, AccommodationEmbeddingRow.class)
                 .setParameter("ids", ids)
                 .getResultList();
    }

    private Map<Long, AccommodationEmbeddingRow> collectBaseInfo(List<AccommodationEmbeddingRow> embeddingRows) {
        return embeddingRows.stream()
                            .collect(Collectors.groupingBy(
                                    AccommodationEmbeddingRow::accommodationId,
                                    Collectors.collectingAndThen(
                                            Collectors.toList(),
                                            list -> list.get(0)
                                    )
                            ));
    }

    private Map<Long, Map<Season, Map<DayType, Integer>>> collectMetadataPrices(List<AccommodationEmbeddingRow> embeddingRows) {
        return embeddingRows.stream()
                            .collect(Collectors.groupingBy(
                                    AccommodationEmbeddingRow::accommodationId,
                                    Collectors.groupingBy(
                                            AccommodationEmbeddingRow::season,
                                            Collectors.toMap(
                                                    AccommodationEmbeddingRow::dayType,
                                                    AccommodationEmbeddingRow::price
                                            )
                                    )
                            ));
    }

    private List<AmenityRow> getAmenityRows(List<Long> ids) {
        return em.createQuery("""
                         SELECT new project.chatbot.adapter.out.ai.model.AmenityRow(
                             acc.id,
                             am.description
                         )
                         FROM Accommodation AS acc
                         LEFT JOIN AccommodationAmenity AS aa ON aa.accommodation = acc
                         JOIN Amenity AS am ON aa.amenityId = am.id
                         WHERE acc.id IN :ids
                         """, AmenityRow.class)
                 .setParameter("ids", ids)
                 .getResultList();
    }

    private Map<Long, List<String>> collectEmbedAmenities(List<AmenityRow> amenityRows) {
        return amenityRows.stream()
                            .collect(Collectors.groupingBy(
                                    AmenityRow::accommodationId,
                                    Collectors.mapping(
                                            AmenityRow::name,
                                            Collectors.toList())
                            ));
    }

    private Map<String, Object> getMetadata(Long id,
                                            Map<Long, Map<Season, Map<DayType, Integer>>> priceInfo,
                                            AccommodationEmbeddingRow row) {

        Map<Season, Map<DayType, Integer>> pricesMap = priceInfo.get(id);

        List<Integer> allPrices = pricesMap.values()
                                           .stream()
                                           .flatMap(dayMap -> dayMap.values().stream())
                                           .toList();

        Integer minPrice = allPrices.stream().min(Integer::compareTo).orElse(0);
        Integer maxPrice = allPrices.stream().max(Integer::compareTo).orElse(0);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("accId", id);
        metadata.put("title", row.title());
        metadata.put("maxPeople", row.maxPeople());
        metadata.put("address", row.address());
        metadata.put("minPrice", minPrice);
        metadata.put("maxPrice", maxPrice);

        return metadata;
    }

    private void afterProcess(List<Long> ids, String message, boolean embedded) {
        if (!ids.isEmpty()) {
            log.info("{} : {}", message, ids);

            em.createQuery("""
                      UPDATE Accommodation AS acc
                      SET acc.isEmbedded = :embedded
                      WHERE acc.id IN :ids
                      """)
              .setParameter("ids", ids)
              .setParameter("embedded", embedded)
              .executeUpdate();
        }
    }
}
