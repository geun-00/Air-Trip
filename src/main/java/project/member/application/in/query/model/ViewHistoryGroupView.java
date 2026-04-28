package project.member.application.in.query.model;

import java.time.LocalDate;
import java.util.List;

public record ViewHistoryGroupView(
        LocalDate date,
        List<ViewHistoryAccommodationView> accommodations
) {
}
