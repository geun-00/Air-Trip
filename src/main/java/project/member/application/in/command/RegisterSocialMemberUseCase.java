package project.member.application.in.command;

import project.member.application.in.command.model.RegisterSocialMemberCommand;

public interface RegisterSocialMemberUseCase {

    void registerSocial(RegisterSocialMemberCommand command);
}
