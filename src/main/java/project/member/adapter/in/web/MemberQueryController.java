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
import project.member.adapter.in.web.response.ChatMemberSearchResponse;
import project.member.adapter.in.web.response.ChatMembersSearchResponse;
import project.member.adapter.in.web.response.DefaultProfileResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.application.in.query.GetMyProfileQueryUseCase;
import project.member.application.in.query.GetMyTripsHistoryQueryUseCase;
import project.member.application.in.query.SearchMembersByNameQueryUseCase;
import project.member.application.in.query.model.ChatMemberSearchView;
import project.member.application.in.query.model.ChatMembersSearchView;
import project.member.application.in.query.model.DefaultProfileView;
import project.member.application.in.query.model.TripHistoryView;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberQueryController {

    private final GetMyProfileQueryUseCase getMyProfileQueryUseCase;
    private final GetMyTripsHistoryQueryUseCase getMyTripsHistoryQueryUseCase;
    private final SearchMembersByNameQueryUseCase searchMembersByNameQueryUseCase;

    @GetMapping("/me")
    public ResponseEntity<DefaultProfileResponse> getMyProfile(@CurrentMemberId Long memberId) {
        DefaultProfileView profile = getMyProfileQueryUseCase.getMyProfile(memberId);
        return ResponseEntity.ok(toResponse(profile));
    }

    private DefaultProfileResponse toResponse(DefaultProfileView profile) {
        return new DefaultProfileResponse(
                profile.name(),
                profile.profileImageUrl(),
                profile.createdDate(),
                profile.aboutMe(),
                profile.isEmailVerified()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ChatMembersSearchResponse> findMembersByName(@RequestParam("name") String name) {
        ChatMembersSearchView search = searchMembersByNameQueryUseCase.findMembersByName(name);
        List<ChatMemberSearchResponse> members = search.members().stream()
                                                       .map(this::toResponse)
                                                       .toList();
        ChatMembersSearchResponse response = new ChatMembersSearchResponse(members);

        return ResponseEntity.ok(response);
    }

    private ChatMemberSearchResponse toResponse(ChatMemberSearchView member) {
        return new ChatMemberSearchResponse(
                member.id(),
                member.name(),
                member.createdDateTime(),
                member.profileImageUrl()
        );
    }

    @GetMapping("/me/trips/past")
    public ResponseEntity<PageResponse<TripHistoryResponse>> getTripsHistory(@CurrentMemberId Long memberId,
                                                                             Pageable pageable) {
        PageResponse<TripHistoryResponse> response = getMyTripsHistoryQueryUseCase.getTripsHistory(memberId, pageable)
                                                                                  .map(this::toResponse);
        return ResponseEntity.ok(response);
    }

    private TripHistoryResponse toResponse(TripHistoryView tripHistory) {
        return new TripHistoryResponse(
                tripHistory.reservationId(),
                tripHistory.accommodationId(),
                tripHistory.thumbnailUrl(),
                tripHistory.title(),
                tripHistory.startDate(),
                tripHistory.endDate(),
                tripHistory.hasReviewed()
        );
    }
}
