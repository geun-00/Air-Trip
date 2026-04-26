package project.accommodation.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import project.accommodation.application.service.AccommodationStatisticsService;

@Component
@RequiredArgsConstructor
public class AccommodationStatisticsScheduler {

    private final AccommodationStatisticsService accommodationStatisticsService;

    @Scheduled(cron = "0 0 2 * * *")
    public void refreshStats() {
        accommodationStatisticsService.refreshStats();
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void refreshRecentStats() {
        accommodationStatisticsService.refreshRecentStats();
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void refreshAllStats() {
        accommodationStatisticsService.refreshAllStats();
    }
}
