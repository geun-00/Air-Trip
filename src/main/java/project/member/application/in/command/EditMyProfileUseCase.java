package project.member.application.in.command;

import project.member.application.in.command.model.EditMyProfileCommand;
import project.member.application.in.command.model.EditProfileResult;

public interface EditMyProfileUseCase {

    EditProfileResult editMyProfile(EditMyProfileCommand command);
}
