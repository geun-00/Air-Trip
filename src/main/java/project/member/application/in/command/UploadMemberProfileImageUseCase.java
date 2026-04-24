package project.member.application.in.command;

import org.springframework.web.multipart.MultipartFile;

public interface UploadMemberProfileImageUseCase {

    void upload(Long memberId, String imageUrl);

    void uploadAndDeleteOrigin(Long memberId, String oldImageUrl, MultipartFile newImageFile);
}
