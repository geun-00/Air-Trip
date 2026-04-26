package project.history.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.history.application.in.query.GetRecentViewHistoryUseCase;
import project.history.application.in.query.model.RecentViewHistoryView;
import project.history.application.out.query.LoadRecentViewHistoryPort;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewHistoryQueryService implements GetRecentViewHistoryUseCase {

    private final LoadRecentViewHistoryPort loadRecentViewHistoryPort;

    @Override
    public List<RecentViewHistoryView> getRecentViewHistories(Long memberId) {
        return loadRecentViewHistoryPort.loadRecentViewHistories(memberId);
    }
}
