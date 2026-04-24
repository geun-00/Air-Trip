package project.history.adapter.in.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import project.history.application.event.ViewHistoryEvent;
import project.history.application.service.ViewHistoryService;

@Component
@RequiredArgsConstructor
public class RecentViewListener {

    private final ViewHistoryService viewHistoryService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecentViewEvent(ViewHistoryEvent event) {
        viewHistoryService.addHistory(event.memberId(), event.accommodationId());
    }
}
