package project.config.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.holiday.adapter.out.api.HolidayApiClient;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;

@Configuration
public class HttpClientTemplateConfig {

    @Bean
    public HttpClientTemplate<TourApiClient> tourApiTemplate(TourApiClient client) {
        return new HttpClientTemplate<>(client);
    }

    @Bean
    public HttpClientTemplate<HolidayApiClient> holidayApiTemplate(HolidayApiClient client) {
        return new HttpClientTemplate<>(client);
    }
}
