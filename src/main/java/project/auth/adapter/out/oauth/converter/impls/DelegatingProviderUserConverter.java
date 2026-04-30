package project.auth.adapter.out.oauth.converter.impls;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import project.auth.adapter.out.oauth.converter.ProviderUserConverter;
import project.auth.adapter.out.oauth.converter.ProviderUserRequest;
import project.auth.adapter.out.oauth.model.ProviderUser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Component
public class DelegatingProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {

    private final List<ProviderUserConverter<ProviderUserRequest, ProviderUser>> converters;

    public DelegatingProviderUserConverter() {
        this.converters = Collections.unmodifiableList(new LinkedList<>(List.of(
                new OAuth2GoogleProviderUserConverter(),
                new OAuth2NaverProviderUserConverter(),
                new OAuth2KakaoOidcProviderUserConverter(),
                new OAuth2KakaoProviderUserConverter(),
                new OAuth2GithubProviderUserConverter()
        )));
    }

    @Override
    public ProviderUser converter(ProviderUserRequest providerUserRequest) {

        Assert.notNull(providerUserRequest, "providerUserRequest cannot be null");

        for (ProviderUserConverter<ProviderUserRequest, ProviderUser> converter : converters) {
            ProviderUser providerUser = converter.converter(providerUserRequest);

            if (providerUser != null) {
                return providerUser;
            }
        }

        return null;
    }
}
