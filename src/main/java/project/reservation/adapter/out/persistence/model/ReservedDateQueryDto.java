package project.reservation.adapter.out.persistence.model;

import java.time.LocalDateTime;

public record ReservedDateQueryDto(LocalDateTime startDate, LocalDateTime endDate) {
}
