package project.common.domain;

import java.time.LocalDate;

public enum Season {
    OFF,
    PEAK;

    public static boolean isFixedPeakMonth(LocalDate date) {
        int month = date.getMonthValue();
        return month == 2 || month == 7 || month == 8 || month == 12;
    }

    public static Season from(boolean isHoliday) {
        return isHoliday ? PEAK : OFF;
    }
}
