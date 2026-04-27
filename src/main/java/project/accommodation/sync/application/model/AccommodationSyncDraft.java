package project.accommodation.sync.application.model;

import lombok.Data;

import static org.springframework.util.StringUtils.hasText;

@Data
public class AccommodationSyncDraft {

    private final AccommodationSyncSeed seed;

    private AccommodationCommonPayload common = new AccommodationCommonPayload();
    private AccommodationIntroPayload intro = new AccommodationIntroPayload();
    private AccommodationInfoPayload info = new AccommodationInfoPayload();
    private AccommodationImagePayload image = new AccommodationImagePayload();

    public boolean hasThumbnail() {
        return hasText(common.getThumbnailUrl())
                || !info.getRoomImgUrls().isEmpty()
                || !image.getOriginImgUrls().isEmpty();
    }

    public boolean hasAllPrices() {
        return info.hasAllPrices();
    }

    public boolean hasMandatoryFields() {
        return hasText(common.getTitle())
                && hasText(seed.contentId())
                && seed.modifiedTime() != null
                && hasText(common.getSigunguCode())
                && hasText(common.getAddress())
                && common.getMapX() != null
                && common.getMapY() != null
                && hasThumbnail()
                && hasAllPrices();
    }
}
