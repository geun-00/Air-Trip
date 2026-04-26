package project.accommodation.adapter.in.web.response;

import java.util.List;

public record MainAccommodationResponse(
        String areaName,
        String areaCode,
        List<MainAccommodationsResponse> accommodations) {
}
