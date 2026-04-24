package project.member.application.in.command;

import project.member.application.in.command.model.RegisterMemberCommand;
import project.member.application.in.command.model.RegisterSocialMemberCommand;

public interface RegisterMemberUseCase {

    void register(RegisterMemberCommand command);

    void registerSocial(RegisterSocialMemberCommand command);
}
