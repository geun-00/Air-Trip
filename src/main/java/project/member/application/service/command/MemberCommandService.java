package project.member.application.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.member.application.in.command.RegisterMemberUseCase;
import project.member.application.in.command.model.RegisterMemberCommand;
import project.member.application.in.command.model.RegisterSocialMemberCommand;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService implements RegisterMemberUseCase {

    @Override
    public void register(RegisterMemberCommand command) {

    }

    @Override
    public void registerSocial(RegisterSocialMemberCommand command) {

    }
}
