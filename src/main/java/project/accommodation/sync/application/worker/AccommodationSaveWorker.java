package project.accommodation.sync.application.worker;

import project.accommodation.domain.Accommodation;
import project.amenity.domain.Amenity;
import project.accommodation.sync.application.model.AccommodationProcessorDto;
import project.accommodation.sync.adapter.out.persistence.TourRepositoryFacadeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.springframework.util.StringUtils.hasText;

public record AccommodationSaveWorker(
        TourRepositoryFacadeManager tourRepositoryFacadeManager,
        List<? extends AccommodationProcessorDto> dtoList,
        Predicate<AccommodationProcessorDto> validator) implements Runnable {

    @Override
    public void run() {
        List<Accommodation> accommodations = new ArrayList<>();

        for (AccommodationProcessorDto dto : dtoList) {
            if (!validator.test(dto)) continue;

            Accommodation acc = tourRepositoryFacadeManager.findAccByContentId(dto.getContentId()).orElseGet(Accommodation::createEmpty);
            tourRepositoryFacadeManager.validateSigunguCode(dto.getSigunguCode());

            acc.updateOrInit(dto);

            accommodations.add(acc);
            acc.replaceAmenities(getAmenityIds(dto));
            acc.replaceImages(findThumbnailUrl(dto), dto.getOriginImgUrls(), dto.getRoomImgUrls());
            acc.replacePrices(dto.getPrices());
        }

        tourRepositoryFacadeManager.saveEntities(accommodations);
    }

    private List<Long> getAmenityIds(AccommodationProcessorDto dto) {
        List<Long> amenityIds = new ArrayList<>();
        addEntityIfAvailable(amenityIds, dto.getIntroAmenities());
        addEntityIfAvailable(amenityIds, dto.getInfoAmenities());
        return amenityIds;
    }

    private void addEntityIfAvailable(List<Long> amenityIds, Map<String, Boolean> amenities) {
        amenities.forEach((amenityName, available) -> {
            if (Boolean.TRUE.equals(available)) {
                Amenity amenity = tourRepositoryFacadeManager.findAmenityByName(amenityName);
                amenityIds.add(amenity.getId());
            }
        });
    }

    private String findThumbnailUrl(AccommodationProcessorDto item) {
        String thumbnailUrl = item.getThumbnailUrl();

        if (!hasText(thumbnailUrl)) {
            if (!item.getOriginImgUrls().isEmpty()) {
                thumbnailUrl = item.getOriginImgUrls().get(0);
            } else {
                thumbnailUrl = item.getRoomImgUrls().get(0);
            }
        }

        return thumbnailUrl;
    }

}
