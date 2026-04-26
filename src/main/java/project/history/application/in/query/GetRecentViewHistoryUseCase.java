package project.history.application.in.query;

import project.history.application.in.query.model.RecentViewHistoryView;

import java.util.List;

public interface GetRecentViewHistoryUseCase {

    List<RecentViewHistoryView> getRecentViewHistories(Long memberId);
}
