package project.history.application.in.query.model;

import java.time.LocalDateTime;

public record RecentViewHistoryView(
        Long accommodationId,
        LocalDateTime viewedAt
) {
}
