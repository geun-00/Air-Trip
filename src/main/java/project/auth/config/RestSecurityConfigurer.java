package project.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import project.auth.adapter.in.security.handler.CustomAuthenticationEntryPoint;
import project.auth.adapter.in.security.handler.RestAuthenticationFailureHandler;
import project.auth.adapter.in.security.handler.RestAuthenticationSuccessHandler;
import project.auth.adapter.in.security.rest.RestApiDsl;

@Configuration
@RequiredArgsConstructor
public class RestSecurityConfigurer extends AbstractHttpConfigurer<RestSecurityConfigurer, HttpSecurity> {

    private final ObjectMapper objectMapper;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
    private final RestAuthenticationFailureHandler restAuthenticationFailureHandler;

    @Override
    public void init(HttpSecurity http) throws Exception {
        http
                .with(new RestApiDsl<>(objectMapper), rest -> rest
                        .restSuccessHandler(restAuthenticationSuccessHandler)
                        .restFailureHandler(restAuthenticationFailureHandler)
                        .loginProcessingUrl("/api/auth/login")
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );
    }
}
