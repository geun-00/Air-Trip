package project.member.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import project.member.adapter.in.web.response.ViewHistoryResponse;
import project.member.application.in.query.ReadMemberProfileUseCase;
import project.member.application.in.query.ReadViewedAccommodationsUseCase;
import project.member.application.in.query.SearchMembersUseCase;
import project.member.application.in.query.model.ChatMembersSearchView;
import project.member.application.in.query.model.DefaultProfileView;

import java.util.List;

import static project.member.adapter.in.web.MemberQueryResponseMapper.toResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberQueryController {

    private final SearchMembersUseCase searchMembersUseCase;
    private final ReadMemberProfileUseCase readMemberProfileUseCase;
    private final ReadViewedAccommodationsUseCase readViewedAccommodationsUseCase;

    @GetMapping("/me")
    public ResponseEntity<DefaultProfileResponse> getMyProfile(@CurrentMemberId Long memberId) {
        DefaultProfileView profile = readMemberProfileUseCase.getMyProfile(memberId);
        return ResponseEntity.ok(toResponse(profile));
    }

    @GetMapping("/search")
    public ResponseEntity<ChatMembersSearchResponse> findMembersByName(@RequestParam("name") String name) {
        ChatMembersSearchView search = searchMembersUseCase.findMembersByName(name);
        return ResponseEntity.ok(toResponse(search));
    }

    @GetMapping("/me/trips/past")
    public ResponseEntity<PageResponse<TripHistoryResponse>> getTripsHistory(
            @CurrentMemberId Long memberId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<TripHistoryResponse> response = PageResponse.from(
                readMemberProfileUseCase.getTripsHistory(memberId, pageable)
                                        .map(MemberQueryResponseMapper::toResponse)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/history/accommodations")
    public ResponseEntity<List<ViewHistoryResponse>> getRecentViewAccommodations(@CurrentMemberId Long memberId) {
        List<ViewHistoryResponse> result = readViewedAccommodationsUseCase.getRecentViewAccommodations(memberId)
                                                                          .stream()
                                                                          .map(MemberQueryResponseMapper::toResponse)
                                                                          .toList();
        return ResponseEntity.ok(result);
    }
}
