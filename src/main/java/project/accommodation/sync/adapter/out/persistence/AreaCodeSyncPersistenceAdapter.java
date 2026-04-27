package project.accommodation.sync.adapter.out.persistence;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.sync.application.model.AreaCodeSyncPayload;
import project.accommodation.sync.application.model.SigunguCodeSyncPayload;
import project.area.adapter.out.persistence.AreaCodeRepository;
import project.area.adapter.out.persistence.SigunguCodeRepository;
import project.area.domain.AreaCode;
import project.area.domain.SigunguCode;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class AreaCodeSyncPersistenceAdapter {

    private EntityManager entityManager;

    private final AreaCodeRepository areaCodeRepository;
    private final SigunguCodeRepository sigunguCodeRepository;

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

    public void replaceSigunguCodes(AreaCode areaCode, List<SigunguCodeSyncPayload> payloads) {
        sigunguCodeRepository.deleteByAreaCode_Code(areaCode.getCode());
        sigunguCodeRepository.flush();
        entityManager.clear();

        AreaCode managedAreaCode = entityManager.getReference(AreaCode.class, areaCode.getCode());
        for (SigunguCodeSyncPayload payload : payloads) {
            entityManager.persist(SigunguCode.create(
                    toSigunguCode(areaCode.getCode(), payload.code()),
                    payload.codeName(),
                    managedAreaCode
            ));
        }
        entityManager.flush();
    }

    private String toSigunguCode(String areaCode, String sigunguCode) {
        return areaCode + "-" + sigunguCode;
    }
}
