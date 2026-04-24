package project.member.adapter.in.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import project.member.application.in.command.RegisterAdminMemberUseCase;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final RegisterAdminMemberUseCase registerAdminMemberUseCase;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        registerAdminMemberUseCase.registerAdmin(adminEmail, adminPassword);
    }
}
