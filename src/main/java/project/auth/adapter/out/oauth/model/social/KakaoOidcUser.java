package project.auth.adapter.out.oauth.model.social;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import project.auth.adapter.out.oauth.model.Attributes;
import project.auth.adapter.out.oauth.model.OAuth2ProviderUser;

public class KakaoOidcUser extends OAuth2ProviderUser {

    public KakaoOidcUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(attributes.getMainAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getUsername() {
        return (String) getAttributes().get("nickname");
    }

    @Override
    public String getImageUrl() {
        return (String) getAttributes().get("picture");
    }

    @Override
    public String getPrincipalName() {
        return (String) getAttributes().get("sub");
    }
}