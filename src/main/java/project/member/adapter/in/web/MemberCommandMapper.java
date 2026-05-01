package project.member.adapter.in.web;

import org.springframework.web.multipart.MultipartFile;
import project.common.application.model.UploadFile;
import project.common.exception.ImageUploadException;
import project.member.adapter.in.web.request.EditProfileRequest;
import project.member.adapter.in.web.request.RegisterMemberRequest;
import project.member.adapter.in.web.response.EditProfileResponse;
import project.member.application.in.command.model.EditMyProfileCommand;
import project.member.application.in.command.model.EditProfileResult;
import project.member.application.in.command.model.ProfileImageChange;
import project.member.application.in.command.model.RegisterMemberCommand;

import java.io.IOException;

final class MemberCommandMapper {

    private MemberCommandMapper() {
    }

    static RegisterMemberCommand toCommand(RegisterMemberRequest request) {
        return new RegisterMemberCommand(
                request.name(),
                request.email(),
                request.number(),
                request.birthDate(),
                request.password()
        );
    }

    static EditMyProfileCommand toCommand(
            Long memberId,
            MultipartFile imageFile,
            EditProfileRequest request
    ) {
        return new EditMyProfileCommand(
                memberId,
                toProfileImageChange(request.isProfileImageChanged(), imageFile),
                request.name(),
                request.aboutMe()
        );
    }

    private static ProfileImageChange toProfileImageChange(boolean changed, MultipartFile imageFile) {
        if (!changed) {
            return ProfileImageChange.noOp();
        }

        if (imageFile == null || imageFile.isEmpty()) {
            return ProfileImageChange.remove();
        }

        try {
            return ProfileImageChange.replace(new UploadFile(
                    imageFile.getOriginalFilename(),
                    imageFile.getContentType(),
                    imageFile.getSize(),
                    imageFile.getBytes()
            ));
        } catch (IOException e) {
            throw new ImageUploadException("Failed to read profile image file", e);
        }
    }

    static EditProfileResponse toResponse(EditProfileResult result) {
        return new EditProfileResponse(result.name(), result.profileImageUrl(), result.aboutMe());
    }
}
