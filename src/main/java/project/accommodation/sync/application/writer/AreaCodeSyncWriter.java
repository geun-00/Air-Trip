package project.accommodation.sync.application.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.accommodation.sync.adapter.out.persistence.AreaCodeSyncPersistenceAdapter;
import project.accommodation.sync.application.model.AreaCodeSyncPayload;
import project.accommodation.sync.application.model.ChildAreaCodeSyncPayload;
import project.area.domain.AreaCode;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AreaCodeSyncWriter {

    private final AreaCodeSyncPersistenceAdapter areaCodeSyncPersistenceAdapter;

    public void write(
            AreaCodeSyncPayload areaCode,
            List<ChildAreaCodeSyncPayload> childAreaCodes
    ) {
        AreaCode savedAreaCode = areaCodeSyncPersistenceAdapter.saveAreaCode(areaCode);
        areaCodeSyncPersistenceAdapter.replaceChildAreaCodes(savedAreaCode, childAreaCodes);
    }
}
