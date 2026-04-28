package project.accommodation.sync.application.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccommodationImagePayload {

    private List<String> originImgUrls = new ArrayList<>();
}
