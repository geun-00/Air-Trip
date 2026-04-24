package project.member.adapter.in.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.member.domain.Member;
import project.member.adapter.out.persistence.MemberRepository;
import project.member.domain.support.AdminMemberCreateSpec;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (memberRepository.existsByEmail(adminEmail)) {
            return;
        }

        Member admin = Member.createAdmin(new AdminMemberCreateSpec(adminEmail, passwordEncoder.encode(adminPassword)));
        memberRepository.save(admin);
    }
}
