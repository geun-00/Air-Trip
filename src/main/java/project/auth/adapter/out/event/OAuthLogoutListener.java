package project.auth.adapter.out.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import project.auth.adapter.out.oauth.client.KakaoAppClient;
import project.auth.adapter.out.oauth.client.KakaoAppClient.KakaoIdResponse;
import project.auth.adapter.out.oauth.client.NaverAppClient;
import project.auth.adapter.out.oauth.client.NaverAppClient.NaverResponse;
import project.auth.application.event.OAuthLogoutEvent;
import project.member.domain.SocialType;
import project.auth.adapter.out.oauth.model.OAuthPrincipal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static project.infrastructure.jwt.JwtProperties.TOKEN_PREFIX;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class OAuthLogoutListener {

    private final KakaoAppClient kakaoAppClient;
    private final NaverAppClient naverAppClient;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @EventListener
    public void handleOAuthLogoutEvent(OAuthLogoutEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof OAuthPrincipal)) {
            return;
        }
        log.debug("OAuthLogoutListener.handleOAuthLogoutEvent: {}", event);

        SocialType socialType = event.socialType();
        switch (socialType) {
            case KAKAO -> {
                String accessToken = getAccessToken(socialType.getSocialName(), authentication);
                KakaoIdResponse response = kakaoAppClient.logout(TOKEN_PREFIX + accessToken);
                log.debug("kakao logout success: response={}", response);
            }
            case NAVER -> {
                String accessToken = getAccessToken(socialType.getSocialName(), authentication);
                String encodedToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);

                NaverResponse response = naverAppClient.logout(encodedToken);
                log.debug("Naver logout success: response={}", response);
            }
            default -> log.debug("Not support logout API: {}", socialType);
        }
    }

    private String getAccessToken(String registrationId, Authentication authentication) {
        OAuthPrincipal oauthPrincipal = (OAuthPrincipal) authentication.getPrincipal();

        String principalName = oauthPrincipal.getPrincipalName();
        Assert.notNull(principalName, "PrincipalName Cannot be null");

        OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(registrationId, principalName);
        return authorizedClient.getAccessToken().getTokenValue();
    }
}
