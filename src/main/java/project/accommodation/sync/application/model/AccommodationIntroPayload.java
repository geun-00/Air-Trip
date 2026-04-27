package project.accommodation.sync.application.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AccommodationIntroPayload {

    private String checkIn;
    private String checkOut;
    private String refundRegulation;
    private final Map<String, Boolean> amenities = new HashMap<>();

    public void putAmenity(String amenity, boolean available) {
        this.amenities.put(amenity, available);
    }
}
