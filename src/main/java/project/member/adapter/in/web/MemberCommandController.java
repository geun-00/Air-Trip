package project.member.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.member.adapter.in.web.request.EditProfileRequest;
import project.member.adapter.in.web.request.RegisterMemberRequest;
import project.member.adapter.in.web.response.EditProfileResponse;
import project.member.application.in.command.ManageMemberUseCase;
import project.member.application.in.command.model.EditProfileResult;

import static project.member.adapter.in.web.MemberCommandMapper.toCommand;
import static project.member.adapter.in.web.MemberCommandMapper.toResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberCommandController {

    private final ManageMemberUseCase manageMemberUseCase;

    @PostMapping
    public ResponseEntity<Void> signup(@Valid @RequestBody RegisterMemberRequest request) {
        manageMemberUseCase.register(toCommand(request));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/me")
    public ResponseEntity<EditProfileResponse> editMyProfile(
            @CurrentMemberId Long memberId,
            @RequestPart(value = "profileImage", required = false) MultipartFile imageFile,
            @Valid @RequestPart("editProfileRequest") EditProfileRequest request
    ) {
        EditProfileResult result = manageMemberUseCase.editMyProfile(toCommand(memberId, imageFile, request));
        EditProfileResponse response = toResponse(result);

        return ResponseEntity.ok(response);
    }
}
