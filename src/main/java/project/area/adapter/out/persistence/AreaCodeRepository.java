package project.area.adapter.out.persistence;

import project.common.adapter.out.persistence.repository.JpaPersistenceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import project.area.domain.AreaCode;

@JpaPersistenceRepository
public interface AreaCodeRepository extends JpaRepository<AreaCode, String> {

    void deleteByParent_Code(String parentCode);
}
