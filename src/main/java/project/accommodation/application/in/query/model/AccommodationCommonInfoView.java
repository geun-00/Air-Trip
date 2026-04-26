package project.accommodation.application.in.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private DetailImageView images;
    private List<String> amenities;
    private List<DetailReviewView> reviews;
}
