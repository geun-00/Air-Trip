package project.member.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.common.adapter.in.web.response.PageResponse;
import project.member.adapter.in.web.request.EditProfileRequest;
import project.member.adapter.in.web.response.ChatMembersSearchResponse;
import project.member.adapter.in.web.response.DefaultProfileResponse;
import project.member.adapter.in.web.response.EditProfileResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.application.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<DefaultProfileResponse> getMyProfile(@CurrentMemberId Long memberId) {
        DefaultProfileResponse response = memberService.getDefaultProfile(memberId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<EditProfileResponse> editMyProfile(@CurrentMemberId Long memberId,
                                                             @RequestPart(value = "profileImage", required = false) MultipartFile imageFile,
                                                             @Valid @RequestPart("editProfileRequest") EditProfileRequest profileReqDto) {
        EditProfileResponse response = memberService.editMyProfile(memberId, imageFile, profileReqDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ChatMembersSearchResponse> findMembersByName(@RequestParam("name") String name) {
        ChatMembersSearchResponse response = memberService.findMembersByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/trips/past")
    public ResponseEntity<PageResponse<TripHistoryResponse>> getTripsHistory(@CurrentMemberId Long memberId,
                                                                             Pageable pageable) {
        PageResponse<TripHistoryResponse> response = memberService.getTripsHistory(memberId, pageable);
        return ResponseEntity.ok(response);
    }
}
