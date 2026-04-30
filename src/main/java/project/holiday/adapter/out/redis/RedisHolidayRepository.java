package project.holiday.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import project.holiday.application.out.HolidayStore;
import project.infrastructure.time.HolidayProvider;

import java.time.LocalDate;
import java.util.List;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;

@Repository
@RequiredArgsConstructor
public class RedisHolidayRepository implements HolidayProvider, HolidayStore {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isHoliday(LocalDate date) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(key(date.getYear()), date.format(BASIC_ISO_DATE))
        );
    }

    @Override
    public boolean hasYear(int year) {
        return redisTemplate.hasKey(key(year));
    }

    @Override
    public void saveHolidays(int year, List<String> holidays) {
        if (holidays == null || holidays.isEmpty()) {
            return;
        }

        redisTemplate.opsForSet().add(key(year), holidays.toArray(String[]::new));
    }

    private String key(int year) {
        return "holidays:" + year;
    }
}
