package project.accommodation.sync.application.worker.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.common.domain.DayType;
import project.common.domain.Season;

import static project.common.domain.DayType.*;
import static project.common.domain.Season.*;

@Getter
@AllArgsConstructor
public enum PriceKey {
    OFF_WEEKDAY("roomoffseasonminfee1", OFF, WEEKDAY),
    OFF_WEEKEND("roomoffseasonminfee2", OFF, WEEKEND),
    PEAK_WEEKDAY("roompeakseasonminfee1", PEAK, WEEKDAY),
    PEAK_WEEKEND("roompeakseasonminfee2", PEAK, WEEKEND);

    private final String key;
    private final Season season;
    private final DayType dayType;
}
