package project.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import project.auth.adapter.out.oauth.converter.ProviderUserConverter;
import project.auth.adapter.out.oauth.converter.ProviderUserRequest;
import project.auth.adapter.out.oauth.model.PrincipalUser;
import project.auth.adapter.out.oauth.model.ProviderUser;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OAuth2UserService<OidcUserRequest, OidcUser> delegate = new OidcUserService();
    private final ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        ClientRegistration clientRegistration = ClientRegistration.withClientRegistration(userRequest.getClientRegistration())
                                                                  .userNameAttributeName("sub")
                                                                  .build();

        OidcUserRequest oidcUserRequest = new OidcUserRequest(
                clientRegistration,
                userRequest.getAccessToken(),
                userRequest.getIdToken(),
                userRequest.getAdditionalParameters()
        );

        OidcUser oidcUser = delegate.loadUser(oidcUserRequest);

        ProviderUserRequest providerUserRequest = new ProviderUserRequest(clientRegistration, oidcUser);
        ProviderUser providerUser = providerUserConverter.converter(providerUserRequest);

        return new PrincipalUser(providerUser);
    }
}
