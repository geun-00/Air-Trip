package project.member.application.in.command;

import project.common.application.model.UploadFile;

public interface UploadMemberProfileImageUseCase {

    void upload(Long memberId, String imageUrl);

    void uploadAndDeleteOrigin(Long memberId, String oldImageUrl, UploadFile newImageFile);
}
