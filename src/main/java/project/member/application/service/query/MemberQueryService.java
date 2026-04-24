package project.member.application.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.common.adapter.in.web.response.PageResponse;
import project.member.adapter.in.web.response.ChatMemberSearchResponse;
import project.member.adapter.in.web.response.ChatMembersSearchResponse;
import project.member.adapter.in.web.response.DefaultProfileResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.adapter.out.persistence.model.DefaultProfileQueryDto;
import project.member.application.in.query.GetMyProfileQueryUseCase;
import project.member.application.in.query.GetMyTripsHistoryQueryUseCase;
import project.member.application.in.query.SearchMembersByNameQueryUseCase;
import project.member.application.out.query.GetMemberProfilePort;
import project.member.application.out.query.GetMemberTripsHistoryPort;
import project.member.application.out.query.SearchMembersPort;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService implements GetMyProfileQueryUseCase,
                                           SearchMembersByNameQueryUseCase,
                                           GetMyTripsHistoryQueryUseCase {

    private final SearchMembersPort searchMembersPort;
    private final GetMemberProfilePort getMemberProfilePort;
    private final GetMemberTripsHistoryPort getMemberTripsHistoryPort;

    @Override
    public DefaultProfileResponse getMyProfile(Long memberId) {
        DefaultProfileQueryDto profileQueryDto = getMemberProfilePort.getDefaultProfile(memberId);
        return DefaultProfileResponse.from(profileQueryDto);
    }

    @Override
    public ChatMembersSearchResponse findMembersByName(String name) {
        List<ChatMemberSearchResponse> members = searchMembersPort.findMembersByName(name);
        return new ChatMembersSearchResponse(members);
    }

    @Override
    public PageResponse<TripHistoryResponse> getTripsHistory(Long memberId, Pageable pageable) {
        return getMemberTripsHistoryPort.getTripsHistory(memberId, pageable);
    }
}
