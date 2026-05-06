package project.config.mongo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import project.common.adapter.out.persistence.repository.MongoPersistenceRepository;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(
        basePackages = "project",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = MongoPersistenceRepository.class
        )
)
public class MongoConfig {
}
