package project.accommodation.sync.adapter.out.persistence.model;

import java.time.LocalDateTime;

public interface AccommodationModifiedTimeRow {
    String getContentId();

    LocalDateTime getModifiedTime();
}
