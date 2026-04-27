package project.accommodation.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.accommodation.adapter.out.persistence.model.DetailAccommodationRow;
import project.review.adapter.out.persistence.model.DetailReviewRow;
import project.accommodation.adapter.out.persistence.model.ImageDataRow;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.application.in.query.model.DetailImageView;
import project.accommodation.application.in.query.model.DetailReviewView;
import project.accommodation.application.out.query.LoadAccommodationCommonInfoSourcePort;
import project.accommodation.domain.exception.AccommodationExceptions;
import project.common.domain.StayDatePolicy;
import project.review.adapter.out.persistence.ReviewQueryRepository;

import java.util.List;

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
        List<String> amenities = accommodationRepository.findAmenitiesByAccommodationId(accommodationId);
        List<DetailReviewRow> reviews = reviewQueryRepository.findReviewsByAccommodationId(accommodationId);
        List<ImageDataRow> images = accommodationRepository.findImagesByAccommodationId(accommodationId);

        String thumbnail = images.stream()
                                 .filter(ImageDataRow::getThumbnail)
                                 .map(ImageDataRow::getImageUrl)
                                 .findFirst()
                                 .orElse(null);
        List<String> others = images.stream()
                                    .filter(row -> !row.getThumbnail())
                                    .map(ImageDataRow::getImageUrl)
                                    .toList();

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
                new DetailImageView(thumbnail, others),
                amenities,
                reviews.stream()
                       .map(row -> new DetailReviewView(
                               row.memberId(),
                               row.memberName(),
                               row.profileUrl(),
                               row.memberCreatedDate(),
                               row.reviewCreatedDate(),
                               row.rating(),
                               row.content()
                       ))
                       .toList()
        );
    }
}
