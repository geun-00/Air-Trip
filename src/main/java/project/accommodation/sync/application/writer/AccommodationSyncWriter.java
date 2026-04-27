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

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class AccommodationSyncWriter {

    private final AccommodationSyncPersistenceAdapter accommodationSyncPersistenceAdapter;

    public void write(List<? extends AccommodationSyncDraft> drafts) {
        List<Accommodation> accommodations = new ArrayList<>();

        for (AccommodationSyncDraft draft : drafts) {
            if (!draft.hasMandatoryFields()) {
                continue;
            }

            Accommodation accommodation = accommodationSyncPersistenceAdapter.findByContentIdOrCreate(draft.getSeed().contentId());
            accommodationSyncPersistenceAdapter.validateSigunguCode(draft.getCommon().getSigunguCode());

            accommodation.updateBasicInfo(
                    draft.getCommon().getMapX(),
                    draft.getCommon().getMapY(),
                    draft.getCommon().getAddress(),
                    draft.getCommon().getTitle(),
                    draft.getSeed().modifiedTime(),
                    draft.getSeed().contentId(),
                    draft.getCommon().getSigunguCode()
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
            accommodation.replaceAmenities(getAmenityIds(draft));
            accommodation.replaceImages(
                    findThumbnailUrl(draft),
                    draft.getImage().getOriginImgUrls(),
                    draft.getInfo().getRoomImgUrls()
            );
            accommodation.replacePrices(draft.getInfo().getPrices());
        }

        accommodationSyncPersistenceAdapter.saveAll(accommodations);
    }

    private List<Long> getAmenityIds(AccommodationSyncDraft draft) {
        List<Long> amenityIds = new ArrayList<>();
        addEntityIfAvailable(amenityIds, draft.getIntro().getAmenities());
        addEntityIfAvailable(amenityIds, draft.getInfo().getAmenities());
        return amenityIds;
    }

    private void addEntityIfAvailable(List<Long> amenityIds, Map<String, Boolean> amenities) {
        amenities.forEach((amenityName, available) -> {
            if (Boolean.TRUE.equals(available)) {
                Amenity amenity = accommodationSyncPersistenceAdapter.findAmenityByName(amenityName);
                amenityIds.add(amenity.getId());
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
