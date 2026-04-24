package project.member.application.in.command;

import project.member.application.command.model.EditProfileResult;
import project.member.application.in.command.model.EditMyProfileCommand;

public interface EditMyProfileUseCase {

    EditProfileResult editMyProfile(EditMyProfileCommand command);
}
