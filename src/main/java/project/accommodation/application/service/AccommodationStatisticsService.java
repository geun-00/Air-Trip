package project.accommodation.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.out.command.RefreshAccommodationStatsPort;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationStatisticsService {

    private final RefreshAccommodationStatsPort refreshAccommodationStatsPort;

    public void refreshStats() {
        log.info("지역별 인기 숙소 TOP N 통계 갱신");
        refreshAccommodationStatsPort.refreshTopStats();
    }

    public void refreshRecentStats() {
        log.info("숙소 반정규화 통계 필드 갱신 - 최근 변경");
        refreshAccommodationStatsPort.refreshRecentStats();
    }

    public void refreshAllStats() {
        log.info("숙소 반정규화 통계 필드 갱신 - 전체");
        refreshAccommodationStatsPort.refreshAllStats();
    }
}
