package project.accommodation.adapter.in.web.response;

import project.accommodation.adapter.in.web.request.ViewHistoryDto;

import java.time.LocalDate;
import java.util.List;

public record ViewHistoryResDto(
        LocalDate date,
        List<ViewHistoryDto> accommodations) {
}
