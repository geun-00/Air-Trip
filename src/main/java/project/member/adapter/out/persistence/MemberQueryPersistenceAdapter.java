package project.member.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.common.adapter.in.web.response.PageResponse;
import project.member.adapter.in.web.response.ChatMemberSearchResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.adapter.out.persistence.model.DefaultProfileQueryDto;
import project.member.application.out.query.GetMemberProfilePort;
import project.member.application.out.query.GetMemberTripsHistoryPort;
import project.member.application.out.query.SearchMembersPort;
import project.member.domain.exception.MemberExceptions;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryPersistenceAdapter implements GetMemberProfilePort,
                                                      SearchMembersPort,
                                                      GetMemberTripsHistoryPort {

    private final MemberQueryRepository memberQueryRepository;

    @Override
    public DefaultProfileQueryDto getDefaultProfile(Long memberId) {
        return memberQueryRepository.getDefaultProfile(memberId)
                                    .orElseThrow(() -> MemberExceptions.notFoundById(memberId));
    }

    @Override
    public List<ChatMemberSearchResponse> findMembersByName(String name) {
        return memberQueryRepository.findMembersByName(name);
    }

    @Override
    public PageResponse<TripHistoryResponse> getTripsHistory(Long memberId, Pageable pageable) {
        Page<TripHistoryResponse> page = memberQueryRepository.getTripsHistory(memberId, pageable);

        return PageResponse.from(page);
    }
}
