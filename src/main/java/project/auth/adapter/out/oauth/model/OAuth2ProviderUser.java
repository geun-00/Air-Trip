package project.auth.adapter.out.oauth.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

public abstract class OAuth2ProviderUser implements ProviderUser {

    protected OAuth2User oAuth2User;
    protected Map<String, Object> attributes;
    protected ClientRegistration clientRegistration;

    protected OAuth2ProviderUser(
            Map<String, Object> attributes,
            OAuth2User oAuth2User,
            ClientRegistration clientRegistration
    ) {
        this.attributes = attributes;
        this.oAuth2User = oAuth2User;
        this.clientRegistration = clientRegistration;
    }

    @Override
    public String getEmail() {
        return (String) getAttributes().get("email");
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities()
                         .stream()
                         .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                         .toList();
    }

    @Override
    public String getProvider() {
        return clientRegistration.getRegistrationId();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
