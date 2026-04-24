package project.member.application.in.command;

import project.member.application.in.command.model.EditMyProfileCommand;
import project.member.adapter.in.web.response.EditProfileResponse;

public interface EditMyProfileUseCase {

    EditProfileResponse editMyProfile(EditMyProfileCommand command);
}
