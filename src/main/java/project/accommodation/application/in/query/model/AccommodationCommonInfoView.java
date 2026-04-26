package project.accommodation.application.in.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.accommodation.adapter.out.persistence.model.DetailAccommodationRow;
import project.accommodation.adapter.out.persistence.model.DetailReviewRow;
import project.accommodation.adapter.out.persistence.model.ImageDataRow;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationCommonInfoView {

    private Long accommodationId;
    private String title;
    private int maxPeople;
    private String address;
    private double mapX;
    private double mapY;
    private String checkIn;
    private String checkOut;
    private String description;
    private String number;
    private String refundRegulation;
    private int price;
    private Double avgRate;
    private AccommodationDetailView.DetailImageView images;
    private List<String> amenities;
    private List<AccommodationDetailView.DetailReviewView> reviews;

    public static AccommodationCommonInfoView from(
            DetailAccommodationRow detail,
            List<String> amenities,
            List<DetailReviewRow> reviews,
            List<ImageDataRow> images
    ) {
        String thumbnail = images.stream()
                                 .filter(ImageDataRow::isThumbnail)
                                 .map(ImageDataRow::imageUrl)
                                 .findFirst()
                                 .orElse(null);
        List<String> others = images.stream()
                                    .filter(row -> !row.isThumbnail())
                                    .map(ImageDataRow::imageUrl)
                                    .toList();

        return new AccommodationCommonInfoView(
                detail.accommodationId(),
                detail.title(),
                detail.maxPeople(),
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
                new AccommodationDetailView.DetailImageView(thumbnail, others),
                amenities,
                reviews.stream()
                       .map(row -> new AccommodationDetailView.DetailReviewView(
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
