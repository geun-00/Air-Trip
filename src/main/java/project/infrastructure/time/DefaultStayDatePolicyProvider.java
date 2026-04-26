package project.infrastructure.time;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.common.domain.DayType;
import project.common.domain.Season;
import project.common.domain.StayDatePolicy;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DefaultStayDatePolicyProvider implements StayDatePolicyProvider {

    private final HolidayProvider holidayProvider;

    @Override
    public StayDatePolicy getStayDatePolicy(LocalDate date) {
        Season season = Season.isFixedPeakMonth(date)
                ? Season.PEAK
                : Season.from(holidayProvider.isHoliday(date));

        return new StayDatePolicy(
                season,
                DayType.from(date)
        );
    }

    @Override
    public StayDatePolicy todayStayDatePolicy() {
        return getStayDatePolicy(LocalDate.now());
    }
}
