package project.member.adapter.in.web.response;

import java.time.LocalDate;
import java.util.List;

public record ViewHistoryResponse(
        LocalDate date,
        List<ViewHistoryAccommodationResponse> accommodations) {
}
