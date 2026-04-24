package project.auth.adapter.out.oauth.converter;

import project.auth.adapter.out.oauth.model.ProviderUser;

public interface ProviderUserConverter<T extends ProviderUserRequest, R extends ProviderUser> {
    R converter(T t);
}
