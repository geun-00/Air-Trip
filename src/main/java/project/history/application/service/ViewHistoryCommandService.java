package project.history.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.history.application.in.command.SaveViewHistoryUseCase;
import project.history.application.out.command.SaveViewHistoryPort;

@Service
@RequiredArgsConstructor
public class ViewHistoryCommandService implements SaveViewHistoryUseCase {

    private final SaveViewHistoryPort saveViewHistoryPort;

    @Override
    public void saveViewHistory(Long memberId, Long accommodationId) {
        saveViewHistoryPort.save(memberId, accommodationId);
    }
}
