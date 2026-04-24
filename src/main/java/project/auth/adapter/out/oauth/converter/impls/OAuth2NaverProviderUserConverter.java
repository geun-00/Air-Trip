package project.auth.adapter.out.oauth.converter.impls;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import project.auth.adapter.out.oauth.converter.ProviderUserConverter;
import project.auth.adapter.out.oauth.converter.ProviderUserRequest;
import project.member.domain.SocialType;
import project.auth.adapter.out.oauth.model.OAuthUtils;
import project.auth.adapter.out.oauth.model.ProviderUser;
import project.auth.adapter.out.oauth.model.social.NaverUser;

public class OAuth2NaverProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {

    @Override
    public ProviderUser converter(ProviderUserRequest providerUserRequest) {
        ClientRegistration clientRegistration = providerUserRequest.clientRegistration();

        if (clientRegistration == null || !clientRegistration.getRegistrationId().equals(SocialType.NAVER.getSocialName())) {
            return null;
        }

        return new NaverUser(
                OAuthUtils.getSubAttributes(providerUserRequest.oAuth2User(), "response"),
                providerUserRequest.oAuth2User(),
                providerUserRequest.clientRegistration()
        );
    }
}
