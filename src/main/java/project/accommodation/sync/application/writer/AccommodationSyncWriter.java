package project.accommodation.sync.application.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.accommodation.domain.Accommodation;
import project.accommodation.sync.adapter.out.persistence.AccommodationSyncPersistenceAdapter;
import project.accommodation.sync.application.model.AccommodationSyncDraft;
import project.amenity.domain.Amenity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class AccommodationSyncWriter {

    private final AccommodationSyncPersistenceAdapter accommodationSyncPersistenceAdapter;

    public void write(List<? extends AccommodationSyncDraft> drafts) {
        List<Accommodation> accommodations = new ArrayList<>();

        List<String> contentIds = drafts.stream()
                                        .map(d -> d.getSeed().contentId())
                                        .toList();
        Map<String, Accommodation> existingMap = accommodationSyncPersistenceAdapter.findAllByContentIdIn(contentIds);

        Set<String> areaCodes = drafts.stream()
                                      .map(d -> d.getCommon().getAreaCode())
                                      .filter(Objects::nonNull)
                                      .collect(Collectors.toSet());
        accommodationSyncPersistenceAdapter.validateAreaCodes(areaCodes);

        Set<String> amenityNames = drafts.stream()
                                         .flatMap(d -> Stream.concat(
                                                 d.getIntro().getAmenities().keySet().stream(),
                                                 d.getInfo().getAmenities().keySet().stream()
                                         ))
                                         .collect(Collectors.toSet());
        Map<String, Amenity> amenityMap = accommodationSyncPersistenceAdapter.findAllAmenitiesByNames(amenityNames);

        for (AccommodationSyncDraft draft : drafts) {
            if (!draft.hasMandatoryFields()) {
                continue;
            }

            Accommodation accommodation = existingMap.getOrDefault(
                    draft.getSeed().contentId(),
                    Accommodation.createEmpty()
            );

            accommodation.updateBasicInfo(
                    draft.getCommon().getMapX(),
                    draft.getCommon().getMapY(),
                    draft.getCommon().getAddress(),
                    draft.getCommon().getTitle(),
                    draft.getSeed().modifiedTime(),
                    draft.getSeed().contentId(),
                    draft.getCommon().getAreaCode()
            );
            accommodation.updateDetail(
                    draft.getCommon().getDescription(),
                    draft.getInfo().getMaxPeople(),
                    draft.getIntro().getCheckIn(),
                    draft.getIntro().getCheckOut(),
                    draft.getCommon().getNumber(),
                    draft.getIntro().getRefundRegulation()
            );

            accommodations.add(accommodation);
            accommodation.replaceAmenities(getAmenityIds(draft, amenityMap));
            accommodation.replaceImages(
                    findThumbnailUrl(draft),
                    draft.getImage().getOriginImgUrls(),
                    draft.getInfo().getRoomImgUrls()
            );
            accommodation.replacePrices(draft.getInfo().getPrices());
        }

        accommodationSyncPersistenceAdapter.saveAll(accommodations);
    }

    private List<Long> getAmenityIds(
            AccommodationSyncDraft draft,
            Map<String, Amenity> amenityMap
    ) {
        List<Long> amenityIds = new ArrayList<>();
        addAmenityIds(amenityIds, draft.getIntro().getAmenities(), amenityMap);
        addAmenityIds(amenityIds, draft.getInfo().getAmenities(), amenityMap);
        return amenityIds;
    }

    private void addAmenityIds(
            List<Long> amenityIds,
            Map<String, Boolean> amenities,
            Map<String, Amenity> amenityMap
    ) {
        amenities.forEach((amenityName, available) -> {
            if (Boolean.TRUE.equals(available)) {
                Amenity amenity = amenityMap.get(amenityName);
                if (amenity != null) {
                    amenityIds.add(amenity.getId());
                }
            }
        });
    }

    private String findThumbnailUrl(AccommodationSyncDraft draft) {
        String thumbnailUrl = draft.getCommon().getThumbnailUrl();

        if (!hasText(thumbnailUrl)) {
            if (!draft.getImage().getOriginImgUrls().isEmpty()) {
                thumbnailUrl = draft.getImage().getOriginImgUrls().getFirst();
            } else {
                thumbnailUrl = draft.getInfo().getRoomImgUrls().getFirst();
            }
        }

        return thumbnailUrl;
    }
}
