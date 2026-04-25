package project.member.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.common.application.model.UploadFile;
import project.common.exception.ImageUploadException;
import project.member.adapter.in.web.request.EditProfileRequest;
import project.member.adapter.in.web.response.EditProfileResponse;
import project.member.application.in.command.EditMyProfileUseCase;
import project.member.application.in.command.model.EditMyProfileCommand;
import project.member.application.in.command.model.EditProfileResult;
import project.member.application.in.command.model.ProfileImageChange;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberCommandController {

    private final EditMyProfileUseCase editMyProfileUseCase;

    @PutMapping("/me")
    public ResponseEntity<EditProfileResponse> editMyProfile(
            @CurrentMemberId Long memberId,
            @RequestPart(value = "profileImage", required = false) MultipartFile imageFile,
            @Valid @RequestPart("editProfileRequest") EditProfileRequest request
    ) {
        EditProfileResult result = editMyProfileUseCase.editMyProfile(new EditMyProfileCommand(
                memberId,
                toProfileImageChange(request.isProfileImageChanged(), imageFile),
                request.name(),
                request.aboutMe()
        ));

        EditProfileResponse response = new EditProfileResponse(result.name(), result.profileImageUrl(), result.aboutMe());
        return ResponseEntity.ok(response);
    }

    private ProfileImageChange toProfileImageChange(boolean changed, MultipartFile imageFile) {
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
}
