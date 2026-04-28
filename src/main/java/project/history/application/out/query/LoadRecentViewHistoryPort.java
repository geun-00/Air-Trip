package project.history.application.out.query;

import project.history.application.in.query.model.RecentViewHistoryView;

import java.util.List;

public interface LoadRecentViewHistoryPort {

    List<RecentViewHistoryView> loadRecentViewHistories(Long memberId);
}
