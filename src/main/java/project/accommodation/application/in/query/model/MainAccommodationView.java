package project.accommodation.application.in.query.model;

import java.util.List;

public record MainAccommodationView(
        String areaName,
        String areaCode,
        List<MainAccommodationItemView> accommodations
) {
}
