package project.member.application.in.command;

import project.member.application.in.command.model.RegisterMemberCommand;

public interface RegisterMemberUseCase {

    void register(RegisterMemberCommand command);
}
