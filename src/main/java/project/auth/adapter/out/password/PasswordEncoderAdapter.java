package project.auth.adapter.out.password;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.member.domain.PasswordMatcher;

@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordMatcher {

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
