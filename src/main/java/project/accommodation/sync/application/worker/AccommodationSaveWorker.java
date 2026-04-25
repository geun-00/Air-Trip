package project.accommodation.sync.application.worker;

import project.accommodation.domain.Accommodation;
import project.accommodation.domain.AccommodationAmenity;
import project.accommodation.domain.AccommodationImage;
import project.accommodation.domain.AccommodationPrice;
import project.amenity.domain.Amenity;
import project.common.domain.DayType;
import project.common.domain.Season;
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
        List<AccommodationAmenity> allAmenities = new ArrayList<>();
        List<AccommodationImage> allImages = new ArrayList<>();
        List<AccommodationPrice> allPrices = new ArrayList<>();

        for (AccommodationProcessorDto dto : dtoList) {
            if (!validator.test(dto)) continue;

            Accommodation acc = tourRepositoryFacadeManager.findAccByContentId(dto.getContentId()).orElseGet(Accommodation::createEmpty);
            tourRepositoryFacadeManager.validateSigunguCode(dto.getSigunguCode());

            acc.updateOrInit(dto);

            accommodations.add(acc);
            addAccommodationAmenity(dto, acc, allAmenities);
            addAccommodationImage(dto, acc, allImages);
            addAccommodationPrice(dto, acc, allPrices);
        }

        tourRepositoryFacadeManager.saveEntities(accommodations, allPrices, allAmenities, allImages);
    }

    private void addAccommodationAmenity(AccommodationProcessorDto dto, Accommodation acc, List<AccommodationAmenity> allAmenities) {
        addEntityIfAvailable(allAmenities, acc, dto.getIntroAmenities());
        addEntityIfAvailable(allAmenities, acc, dto.getInfoAmenities());
    }

    private void addEntityIfAvailable(List<AccommodationAmenity> allAmenities, Accommodation acc, Map<String, Boolean> amenities) {
        amenities.forEach((amenityName, available) -> {
            if (Boolean.TRUE.equals(available)) {
                Amenity amenity = tourRepositoryFacadeManager.findAmenityByName(amenityName);
                allAmenities.add(AccommodationAmenity.create(acc, amenity));
            }
        });
    }

    private void addAccommodationImage(AccommodationProcessorDto item, Accommodation acc, List<AccommodationImage> allImages) {
        String thumbnailUrl = item.getThumbnailUrl();

        if (!hasText(thumbnailUrl)) {
            if (!item.getOriginImgUrls().isEmpty()) {
                thumbnailUrl = item.getOriginImgUrls().get(0);
            } else {
                thumbnailUrl = item.getRoomImgUrls().get(0);
            }
        }

        allImages.add(AccommodationImage.thumbnailOf(acc, thumbnailUrl));

        addImageEntity(item.getOriginImgUrls(), thumbnailUrl, allImages, acc);
        addImageEntity(item.getRoomImgUrls(), thumbnailUrl, allImages, acc);
    }

    private void addImageEntity(List<String> item, String thumbnail, List<AccommodationImage> allImages, Accommodation acc) {
        for (String imageUrl : item) {
            if (imageUrl.equals(thumbnail)) {
                continue;
            }
            allImages.add(AccommodationImage.normalOf(acc, imageUrl));
        }
    }

    private void addAccommodationPrice(AccommodationProcessorDto dto, Accommodation acc, List<AccommodationPrice> allPrices) {
        for (Season season : Season.values()) {
            for (DayType dayType : DayType.values()) {
                allPrices.add(AccommodationPrice.create(acc, season, dayType, dto.getPrice(season, dayType)));
            }
        }
    }
}
