package project.auth.adapter.out.oauth.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ProviderUser {

    String getUsername();

    String getEmail();

    String getImageUrl();

    String getProvider();

    List<? extends GrantedAuthority> getAuthorities();

    Map<String, Object> getAttributes();

    /**
     * {@link OAuth2AuthorizedClientService}.loadAuthorizedClient()에 사용될 principalName(식별자)
     */
    String getPrincipalName();

    default LocalDate getBirthDate() {
        return null;
    }

    default String getNumber() {
        return null;
    }
}
