package project.member.application.in.command;

import project.member.application.in.command.model.UploadMemberProfileImageCommand;

public interface UploadMemberProfileImageUseCase {

    void upload(UploadMemberProfileImageCommand command);
}
