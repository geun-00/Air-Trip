package project.member.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.member.application.in.query.model.ChatMemberSearchView;
import project.member.application.in.query.model.ChatMembersSearchView;
import project.member.application.in.query.model.DefaultProfileView;
import project.member.application.in.query.model.TripHistoryView;
import project.member.application.out.query.ReadMemberProfilePort;
import project.member.application.out.query.SearchMembersPort;
import project.member.domain.exception.MemberExceptions;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryAdapter implements ReadMemberProfilePort, SearchMembersPort {

    private final MemberQueryRepository memberQueryRepository;

    @Override
    public DefaultProfileView getDefaultProfile(Long memberId) {
        return memberQueryRepository.getDefaultProfile(memberId)
                                    .map(MemberQueryViewMapper::toView)
                                    .orElseThrow(() -> MemberExceptions.notFoundById(memberId));
    }

    @Override
    public ChatMembersSearchView findMembersByName(String name) {
        List<ChatMemberSearchView> members = memberQueryRepository.findMembersByName(name)
                                                                  .stream()
                                                                  .map(MemberQueryViewMapper::toView)
                                                                  .toList();
        return new ChatMembersSearchView(members);
    }

    @Override
    public Page<TripHistoryView> getTripsHistory(Long memberId, Pageable pageable) {
        return memberQueryRepository.getTripsHistory(memberId, pageable)
                                    .map(MemberQueryViewMapper::toView);
    }
}
