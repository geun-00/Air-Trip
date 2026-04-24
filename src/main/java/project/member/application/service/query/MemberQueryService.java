package project.member.application.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.common.adapter.in.web.response.PageResponse;
import project.member.adapter.in.web.response.ChatMemberSearchResponse;
import project.member.adapter.in.web.response.ChatMembersSearchResponse;
import project.member.adapter.in.web.response.DefaultProfileResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.adapter.out.persistence.MemberQueryRepository;
import project.member.adapter.out.persistence.model.DefaultProfileQueryDto;
import project.member.application.in.query.GetMyProfileQueryUseCase;
import project.member.application.in.query.GetMyTripsHistoryQueryUseCase;
import project.member.application.in.query.SearchMembersByNameQueryUseCase;
import project.member.domain.exception.MemberExceptions;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService implements GetMyProfileQueryUseCase,
                                           SearchMembersByNameQueryUseCase,
                                           GetMyTripsHistoryQueryUseCase {

    private final MemberQueryRepository memberQueryRepository;

    @Override
    public DefaultProfileResponse getMyProfile(Long memberId) {
        DefaultProfileQueryDto profileQueryDto = memberQueryRepository.getDefaultProfile(memberId)
                                                                      .orElseThrow(() -> MemberExceptions.notFoundById(memberId));
        return DefaultProfileResponse.from(profileQueryDto);
    }

    @Override
    public ChatMembersSearchResponse findMembersByName(String name) {
        List<ChatMemberSearchResponse> members = memberQueryRepository.findMembersByName(name);
        return new ChatMembersSearchResponse(members);
    }

    @Override
    public PageResponse<TripHistoryResponse> getTripsHistory(Long memberId, Pageable pageable) {
        Page<TripHistoryResponse> result = memberQueryRepository.getTripsHistory(memberId, pageable);

        return PageResponse.<TripHistoryResponse>builder()
                           .contents(result.getContent())
                           .pageNumber(pageable.getPageNumber())
                           .pageSize(pageable.getPageSize())
                           .total(result.getTotalElements())
                           .build();
    }
}
