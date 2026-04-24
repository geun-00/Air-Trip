package project.auth.adapter.out.oauth.converter.impls;

import project.auth.adapter.out.oauth.converter.ProviderUserConverter;
import project.auth.adapter.out.oauth.converter.ProviderUserRequest;
import project.member.domain.Member;
import project.auth.adapter.out.oauth.model.ProviderUser;
import project.auth.adapter.out.oauth.model.RestUser;

public class RestProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {

    @Override
    public ProviderUser converter(ProviderUserRequest providerUserRequest) {

        Member member = providerUserRequest.member();

        if (member == null) {
            return null;
        }

        return RestUser.builder()
                       .username(member.getEmail())
                       .email(member.getEmail())
                       .password(member.getPassword())
                       .provider("none")
                       .build();
    }
}
