package project.area.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import project.area.domain.AreaCode;

public interface AreaCodeRepository extends JpaRepository<AreaCode, String> {

    void deleteByParent_Code(String parentCode);
}
