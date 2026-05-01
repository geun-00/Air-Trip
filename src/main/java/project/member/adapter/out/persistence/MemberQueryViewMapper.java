package project.member.adapter.out.persistence;

import project.member.adapter.out.persistence.model.ChatMemberSearchRow;
import project.member.adapter.out.persistence.model.DefaultProfileRow;
import project.member.adapter.out.persistence.model.TripHistoryRow;
import project.member.application.in.query.model.ChatMemberSearchView;
import project.member.application.in.query.model.DefaultProfileView;
import project.member.application.in.query.model.TripHistoryView;

final class MemberQueryViewMapper {

    private MemberQueryViewMapper() {
    }

    static DefaultProfileView toView(DefaultProfileRow row) {
        return new DefaultProfileView(
                row.name().value(),
                row.profileImageUrl(),
                row.createdDateTime().toLocalDate(),
                row.aboutMe() == null ? null : row.aboutMe().value(),
                row.isEmailVerified()
        );
    }

    static ChatMemberSearchView toView(ChatMemberSearchRow row) {
        return new ChatMemberSearchView(
                row.id(),
                row.name().value(),
                row.createdDateTime(),
                row.profileImageUrl()
        );
    }

    static TripHistoryView toView(TripHistoryRow row) {
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
