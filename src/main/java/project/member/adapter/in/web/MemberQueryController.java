package project.member.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.common.adapter.in.web.response.PageResponse;
import project.member.adapter.in.web.response.ChatMembersSearchResponse;
import project.member.adapter.in.web.response.DefaultProfileResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.application.in.query.GetMyProfileQueryUseCase;
import project.member.application.in.query.GetMyTripsHistoryQueryUseCase;
import project.member.application.in.query.SearchMembersByNameQueryUseCase;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberQueryController {

    private final GetMyProfileQueryUseCase getMyProfileQueryUseCase;
    private final GetMyTripsHistoryQueryUseCase getMyTripsHistoryQueryUseCase;
    private final SearchMembersByNameQueryUseCase searchMembersByNameQueryUseCase;

    @GetMapping("/me")
    public ResponseEntity<DefaultProfileResponse> getMyProfile(@CurrentMemberId Long memberId) {
        DefaultProfileResponse response = getMyProfileQueryUseCase.getMyProfile(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ChatMembersSearchResponse> findMembersByName(@RequestParam("name") String name) {
        ChatMembersSearchResponse response = searchMembersByNameQueryUseCase.findMembersByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/trips/past")
    public ResponseEntity<PageResponse<TripHistoryResponse>> getTripsHistory(@CurrentMemberId Long memberId,
                                                                             Pageable pageable) {
        PageResponse<TripHistoryResponse> response = getMyTripsHistoryQueryUseCase.getTripsHistory(memberId, pageable);
        return ResponseEntity.ok(response);
    }
}
