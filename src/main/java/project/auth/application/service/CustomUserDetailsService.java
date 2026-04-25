package project.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.auth.adapter.out.oauth.converter.ProviderUserConverter;
import project.auth.adapter.out.oauth.converter.ProviderUserRequest;
import project.auth.adapter.out.oauth.model.PrincipalUser;
import project.auth.adapter.out.oauth.model.ProviderUser;
import project.member.adapter.out.persistence.MemberRepository;
import project.member.domain.Email;
import project.member.domain.Member;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(new Email(username))
                                        .orElseThrow(() -> new UsernameNotFoundException("email=" + username + " 사용자 조회 실패"));

        ProviderUserRequest providerUserRequest = new ProviderUserRequest(member);
        ProviderUser providerUser = providerUserConverter.converter(providerUserRequest);

        return new PrincipalUser(providerUser);
    }
}
