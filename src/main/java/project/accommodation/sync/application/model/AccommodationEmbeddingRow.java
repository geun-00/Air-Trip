package project.accommodation.sync.application.model;

import project.accommodation.domain.Capacity;
import project.common.domain.DayType;
import project.common.domain.Season;

public interface AccommodationEmbeddingRow {
    Long getAccommodationId();

    String getTitle();

    String getDescription();

    Capacity getMaxPeople();

    String getAddress();

    String getAreaName();

    String getSigunguName();

    Season getSeason();

    DayType getDayType();

    int getPrice();

}
