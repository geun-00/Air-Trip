package project.holiday.application.out;

import java.util.List;

public interface HolidayStore {

    boolean hasYear(int year);

    void saveHolidays(int year, List<String> holidays);
}
