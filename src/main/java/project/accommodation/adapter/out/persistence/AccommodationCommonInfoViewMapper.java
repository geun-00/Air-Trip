package project.accommodation.adapter.out.persistence;

import project.accommodation.adapter.out.persistence.model.DetailAccommodationRow;
import project.accommodation.adapter.out.persistence.model.ImageDataRow;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.application.in.query.model.DetailImageView;
import project.accommodation.application.in.query.model.DetailReviewView;
import project.review.adapter.out.persistence.model.DetailReviewRow;

import java.util.List;

final class AccommodationCommonInfoViewMapper {

    private AccommodationCommonInfoViewMapper() {
    }

    static AccommodationCommonInfoView toView(
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
                new DetailImageView(thumbnail, others),
                amenities,
                toReviewViews(reviews)
        );
    }

    private static String getThumbnail(List<ImageDataRow> images) {
        return images.stream()
                     .filter(ImageDataRow::getThumbnail)
                     .map(ImageDataRow::getImageUrl)
                     .findFirst()
                     .orElse(null);
    }

    private static List<String> getOtherImages(List<ImageDataRow> images) {
        return images.stream()
                     .filter(row -> !row.getThumbnail())
                     .map(ImageDataRow::getImageUrl)
                     .toList();
    }

    private static List<DetailReviewView> toReviewViews(List<DetailReviewRow> reviews) {
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
