package project.holiday.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.auth.adapter.out.redis.RedisRepository;
import project.holiday.application.out.HolidayStore;
import project.infrastructure.time.HolidayProvider;

import java.time.LocalDate;
import java.util.List;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;

@Repository
@RequiredArgsConstructor
public class RedisHolidayRepository implements HolidayProvider, HolidayStore {

    private final RedisRepository redisRepository;

    @Override
    public boolean isHoliday(LocalDate date) {
        return redisRepository.isMemberOfSet(key(date.getYear()), date.format(BASIC_ISO_DATE));
    }

    @Override
    public boolean hasYear(int year) {
        return redisRepository.hasKey(key(year));
    }

    @Override
    public void saveHolidays(int year, List<String> holidays) {
        redisRepository.addSet(key(year), holidays);
    }

    private String key(int year) {
        return "holidays:" + year;
    }
}
