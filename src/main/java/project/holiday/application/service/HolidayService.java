package project.holiday.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.holiday.adapter.out.api.HolidayApiClient;
import project.holiday.application.out.HolidayStore;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayStore holidayStore;
    private final HttpClientTemplate<HolidayApiClient> clientTemplate;

    @Retryable(retryFor = {RuntimeException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public void initHolidays() {
        int year = LocalDate.now().getYear();

        if (holidayStore.hasYear(year)) {
            return;
        }

        List<Map<String, String>> items = clientTemplate.fetchItems(client -> client.getHolidays(year));
        List<String> holidays = items.stream()
                                     .map(map -> map.get("locdate"))
                                     .toList();
        holidayStore.saveHolidays(year, holidays);
    }

    @Recover
    public void recover(RuntimeException e) {
        log.error("공공데이터(공휴일) API 초기화 실패: ", e);
    }
}
