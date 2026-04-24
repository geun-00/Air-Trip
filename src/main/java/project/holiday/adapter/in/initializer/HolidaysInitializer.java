package project.holiday.adapter.in.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import project.holiday.application.service.HolidayService;

@Profile({"local", "prod"})
@Component
@RequiredArgsConstructor
public class HolidaysInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final HolidayService holidayService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        holidayService.initHolidays();
    }
}
