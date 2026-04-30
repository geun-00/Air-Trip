package project.auth.adapter.out.oauth.converter;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public record ProviderUserRequest(
        ClientRegistration clientRegistration,
        OAuth2User oAuth2User
) {
}
