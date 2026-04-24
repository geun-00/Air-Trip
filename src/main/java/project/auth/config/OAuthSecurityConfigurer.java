package project.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import project.auth.config.handler.failer.OAuthAuthenticationFailureHandler;
import project.auth.config.handler.success.OAuthAuthenticationSuccessHandler;
import project.auth.application.service.CustomOAuth2UserService;
import project.auth.application.service.CustomOidcUserService;

@Configuration
@RequiredArgsConstructor
public class OAuthSecurityConfigurer extends AbstractHttpConfigurer<OAuthSecurityConfigurer, HttpSecurity> {

    private final CustomOidcUserService customOidcUserService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler;
    private final OAuthAuthenticationFailureHandler OAuthAuthenticationFailureHandler;

    @Override
    public void init(HttpSecurity http) throws Exception {
        http
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                                .oidcUserService(customOidcUserService)
                        )
                        .successHandler(oAuthAuthenticationSuccessHandler)
                        .failureHandler(OAuthAuthenticationFailureHandler)
                );
    }
}
