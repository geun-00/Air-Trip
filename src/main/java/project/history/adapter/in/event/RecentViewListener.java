package project.history.adapter.in.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import project.history.application.event.ViewHistoryEvent;
import project.history.application.in.command.SaveViewHistoryUseCase;

@Component
@RequiredArgsConstructor
public class RecentViewListener {

    private final SaveViewHistoryUseCase saveViewHistoryUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecentViewEvent(ViewHistoryEvent event) {
        saveViewHistoryUseCase.saveViewHistory(event.memberId(), event.accommodationId());
    }
}
