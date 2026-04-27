package project.accommodation.sync.application.model;

import lombok.Data;

@Data
public class AccommodationCommonPayload {

    private String number;
    private String title;
    private String thumbnailUrl;
    private String sigunguCode;
    private String address;
    private String description;
    private Double mapX;
    private Double mapY;
}
