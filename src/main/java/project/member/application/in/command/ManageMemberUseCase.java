package project.member.application.in.command;

import project.member.application.in.command.model.EditMyProfileCommand;
import project.member.application.in.command.model.EditProfileResult;
import project.member.application.in.command.model.RegisterMemberCommand;

public interface ManageMemberUseCase {

    void register(RegisterMemberCommand command);

    EditProfileResult editMyProfile(EditMyProfileCommand command);
}
