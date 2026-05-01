package project.member.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.common.adapter.in.web.response.PageResponse;
import project.member.adapter.out.persistence.model.ChatMemberSearchRow;
import project.member.adapter.out.persistence.model.DefaultProfileRow;
import project.member.adapter.out.persistence.model.TripHistoryRow;
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
                                    .map(this::convertToView)
                                    .orElseThrow(() -> MemberExceptions.notFoundById(memberId));
    }

    private DefaultProfileView convertToView(DefaultProfileRow row) {
        return new DefaultProfileView(
                row.name().value(),
                row.profileImageUrl(),
                row.createdDateTime().toLocalDate(),
                row.aboutMe() == null ? null : row.aboutMe().value(),
                row.isEmailVerified()
        );
    }

    @Override
    public ChatMembersSearchView findMembersByName(String name) {
        List<ChatMemberSearchView> members = memberQueryRepository.findMembersByName(name)
                                                                  .stream()
                                                                  .map(this::convertToView)
                                                                  .toList();
        return new ChatMembersSearchView(members);
    }

    private ChatMemberSearchView convertToView(ChatMemberSearchRow row) {
        return new ChatMemberSearchView(
                row.id(),
                row.name().value(),
                row.createdDateTime(),
                row.profileImageUrl()
        );
    }

    @Override
    public PageResponse<TripHistoryView> getTripsHistory(Long memberId, Pageable pageable) {
        Page<TripHistoryView> historyViews = memberQueryRepository.getTripsHistory(memberId, pageable)
                                                                  .map(this::convertToView);
        return PageResponse.from(historyViews);
    }

    private TripHistoryView convertToView(TripHistoryRow row) {
        return new TripHistoryView(
                row.reservationId(),
                row.accommodationId(),
                row.thumbnailUrl(),
                row.title(),
                row.startDate().toLocalDate(),
                row.endDate().minusNanos(1).toLocalDate(),
                row.hasReviewed()
        );
    }
}
