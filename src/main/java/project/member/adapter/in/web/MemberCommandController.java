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
import project.member.adapter.in.web.request.EditProfileRequest;
import project.member.adapter.in.web.response.EditProfileResponse;
import project.member.application.in.command.EditMyProfileUseCase;
import project.member.application.in.command.model.EditMyProfileCommand;
import project.member.application.in.command.model.EditProfileResult;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberCommandController {

    private final EditMyProfileUseCase editMyProfileUseCase;

    @PutMapping("/me")
    public ResponseEntity<EditProfileResponse> editMyProfile(@CurrentMemberId Long memberId,
                                                             @RequestPart(value = "profileImage", required = false) MultipartFile imageFile,
                                                             @Valid @RequestPart("editProfileRequest") EditProfileRequest profileReqDto) {
        EditProfileResult result = editMyProfileUseCase.editMyProfile(new EditMyProfileCommand(
                memberId,
                imageFile,
                profileReqDto.name(),
                profileReqDto.aboutMe(),
                profileReqDto.isProfileImageChanged()
        ));

        EditProfileResponse response = new EditProfileResponse(result.name(), result.profileImageUrl(), result.aboutMe());
        return ResponseEntity.ok(response);
    }
}
