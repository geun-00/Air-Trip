package project.accommodation.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.accommodation.adapter.out.persistence.model.AmenityDataRow;
import project.accommodation.adapter.out.persistence.model.DetailAccommodationRow;
import project.accommodation.adapter.out.persistence.model.ImageDataRow;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.application.in.query.model.DetailImageView;
import project.accommodation.application.in.query.model.DetailReviewView;
import project.accommodation.application.out.query.LoadAccommodationCommonInfoSourcePort;
import project.accommodation.domain.exception.AccommodationExceptions;
import project.common.domain.StayDatePolicy;
import project.review.adapter.out.persistence.ReviewQueryRepository;
import project.review.adapter.out.persistence.model.DetailReviewRow;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class AccommodationCommonInfoPersistenceAdapter implements LoadAccommodationCommonInfoSourcePort {

    private final ReviewQueryRepository reviewQueryRepository;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationQueryRepository accommodationQueryRepository;

    @Override
    public AccommodationCommonInfoView loadAccommodationCommonInfo(Long accommodationId, StayDatePolicy stayDatePolicy) {
        DetailAccommodationRow detail = accommodationQueryRepository.findAccommodation(accommodationId, null, stayDatePolicy)
                                                                    .orElseThrow(() -> AccommodationExceptions.notFoundById(accommodationId));
        List<String> amenities = accommodationRepository.findAmenitiesByAccommodationId(accommodationId).stream()
                                                        .map(AmenityDataRow::getDescription)
                                                        .toList();
        List<DetailReviewRow> reviews = reviewQueryRepository.findReviewsByAccommodationId(accommodationId);
        List<ImageDataRow> images = accommodationRepository.findImagesByAccommodationId(accommodationId);

        return toView(detail, amenities, images, reviews);
    }


    @Override
    public Map<Long, AccommodationCommonInfoView> loadAccommodationCommonInfos(List<Long> accommodationIds, StayDatePolicy stayDatePolicy) {
        Map<Long, DetailAccommodationRow> detailMap = accommodationQueryRepository.findAccommodations(accommodationIds, stayDatePolicy)
                                                                                  .stream()
                                                                                  .collect(toMap(
                                                                                          DetailAccommodationRow::accommodationId,
                                                                                          row -> row
                                                                                  ));

        Map<Long, List<AmenityDataRow>> amenitiesMap = accommodationRepository.findAmenitiesByAccommodationIdIn(accommodationIds)
                                                                              .stream()
                                                                              .collect(groupingBy(AmenityDataRow::getAccommodationId));

        Map<Long, List<DetailReviewRow>> reviewsMap = reviewQueryRepository.findReviewsByAccommodationIdIn(accommodationIds)
                                                                           .stream()
                                                                           .collect(groupingBy(DetailReviewRow::accommodationId));

        Map<Long, List<ImageDataRow>> imagesMap = accommodationRepository.findImagesByAccommodationIdIn(accommodationIds)
                                                                         .stream()
                                                                         .collect(groupingBy(ImageDataRow::getAccommodationId));

        return accommodationIds.stream()
                               .filter(detailMap::containsKey)
                               .collect(toMap(
                                       id -> id,
                                       id -> toView(
                                               detailMap.get(id),
                                               amenitiesMap.getOrDefault(id, List.of())
                                                           .stream()
                                                           .map(AmenityDataRow::getDescription)
                                                           .toList(),
                                               imagesMap.getOrDefault(id, List.of()),
                                               reviewsMap.getOrDefault(id, List.of())
                                       )
                               ));
    }

    private AccommodationCommonInfoView toView(
            DetailAccommodationRow detail,
            List<String> amenities,
            List<ImageDataRow> images,
            List<DetailReviewRow> reviews
    ) {
        String thumbnail = getThumbnail(images);
        List<String> others = getOtherImages(images);

        return new AccommodationCommonInfoView(
                detail.accommodationId(),
                detail.title(),
                detail.capacity().value(),
                detail.address(),
                detail.mapX(),
                detail.mapY(),
                detail.checkIn(),
                detail.checkOut(),
                detail.description(),
                detail.number(),
                detail.refundRegulation(),
                detail.price(),
                detail.avgRate(),
                new DetailImageView(thumbnail, others), amenities,
                toView(reviews)
        );
    }

    private String getThumbnail(List<ImageDataRow> images) {
        return images.stream()
                     .filter(ImageDataRow::getThumbnail)
                     .map(ImageDataRow::getImageUrl)
                     .findFirst()
                     .orElse(null);
    }

    private List<String> getOtherImages(List<ImageDataRow> images) {
        return images.stream()
                     .filter(row -> !row.getThumbnail())
                     .map(ImageDataRow::getImageUrl)
                     .toList();
    }

    private List<DetailReviewView> toView(List<DetailReviewRow> reviews) {
        return reviews.stream()
                      .map(row -> new DetailReviewView(
                              row.memberId(),
                              row.memberName(),
                              row.profileUrl(),
                              row.memberCreatedDate(),
                              row.reviewCreatedDate(),
                              row.rating().value(),
                              row.content()
                      ))
                      .toList();
    }
}
