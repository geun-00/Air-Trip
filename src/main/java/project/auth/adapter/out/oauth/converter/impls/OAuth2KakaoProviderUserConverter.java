package project.auth.adapter.out.oauth.converter.impls;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import project.auth.adapter.out.oauth.converter.ProviderUserConverter;
import project.auth.adapter.out.oauth.converter.ProviderUserRequest;
import project.member.domain.SocialType;
import project.auth.adapter.out.oauth.model.OAuthUtils;
import project.auth.adapter.out.oauth.model.ProviderUser;
import project.auth.adapter.out.oauth.model.social.KakaoUser;

public class OAuth2KakaoProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {

    @Override
    public ProviderUser converter(ProviderUserRequest providerUserRequest) {
        ClientRegistration clientRegistration = providerUserRequest.clientRegistration();

        if (clientRegistration == null || !clientRegistration.getRegistrationId().equals(SocialType.KAKAO.getSocialName())) {
            return null;
        }

        if (providerUserRequest.oAuth2User() instanceof OidcUser) {
            return null;
        }

        return new KakaoUser(
                OAuthUtils.getOtherAttributes(providerUserRequest.oAuth2User(), "kakao_account", "profile"),
                providerUserRequest.oAuth2User(),
                clientRegistration
        );
    }
}
