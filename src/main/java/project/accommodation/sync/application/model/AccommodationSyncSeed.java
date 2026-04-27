package project.accommodation.sync.application.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AccommodationSyncSeed(
        String contentId,
        LocalDateTime modifiedTime
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static AccommodationSyncSeed of(String contentId, String modifiedTime) {
        return new AccommodationSyncSeed(contentId, LocalDateTime.parse(modifiedTime, FORMATTER));
    }
}
