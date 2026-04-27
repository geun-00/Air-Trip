package project.accommodation.sync.adapter.out.persistence;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.sync.application.model.AreaCodeSyncPayload;
import project.accommodation.sync.application.model.ChildAreaCodeSyncPayload;
import project.area.adapter.out.persistence.AreaCodeRepository;
import project.area.domain.AreaCode;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class AreaCodeSyncPersistenceAdapter {

    private final EntityManager entityManager;
    private final AreaCodeRepository areaCodeRepository;

    public AreaCode saveAreaCode(AreaCodeSyncPayload payload) {
        return areaCodeRepository.findById(payload.code())
                                 .map(areaCode -> {
                                     areaCode.changeCodeName(payload.codeName());
                                     return areaCode;
                                 })
                                 .orElseGet(() -> areaCodeRepository.save(
                                         AreaCode.create(payload.code(), payload.codeName())
                                 ));
    }

    public void replaceChildAreaCodes(AreaCode areaCode, List<ChildAreaCodeSyncPayload> payloads) {
        areaCodeRepository.deleteByParent_Code(areaCode.getCode());
        areaCodeRepository.flush();
        entityManager.clear();

        AreaCode managedAreaCode = entityManager.getReference(AreaCode.class, areaCode.getCode());
        for (ChildAreaCodeSyncPayload payload : payloads) {
            entityManager.persist(AreaCode.create(
                    composeChildAreaCode(areaCode.getCode(), payload.code()),
                    payload.codeName(),
                    managedAreaCode
            ));
        }
        entityManager.flush();
    }

    private String composeChildAreaCode(String parentAreaCode, String childCode) {
        return parentAreaCode + "-" + childCode;
    }
}
