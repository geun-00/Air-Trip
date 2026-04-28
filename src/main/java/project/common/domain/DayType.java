package project.common.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

public enum DayType {
    WEEKDAY,
    WEEKEND;

    public static DayType from(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        if (dayOfWeek == SATURDAY || dayOfWeek == SUNDAY) {
            return WEEKEND;
        }

        return WEEKDAY;
    }
}
