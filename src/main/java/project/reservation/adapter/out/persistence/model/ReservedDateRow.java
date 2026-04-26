package project.reservation.adapter.out.persistence.model;

import java.time.LocalDateTime;

public record ReservedDateRow(LocalDateTime startDate, LocalDateTime endDate) {
}
