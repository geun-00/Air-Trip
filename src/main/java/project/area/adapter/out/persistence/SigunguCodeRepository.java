package project.area.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import project.area.domain.SigunguCode;

public interface SigunguCodeRepository extends JpaRepository<SigunguCode, String> {
}