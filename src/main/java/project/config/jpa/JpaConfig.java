package project.config.jpa;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import project.common.adapter.out.persistence.repository.JpaPersistenceRepository;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackages = "project",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = JpaPersistenceRepository.class
        )
)
public class JpaConfig {
}
