package project.infrastructure.time;

import java.time.LocalDate;

public interface HolidayProvider {

    boolean isHoliday(LocalDate date);
}
