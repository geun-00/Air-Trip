package project.member.adapter.in.web;

import project.member.adapter.in.web.response.ChatMemberSearchResponse;
import project.member.adapter.in.web.response.ChatMembersSearchResponse;
import project.member.adapter.in.web.response.DefaultProfileResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.adapter.in.web.response.ViewHistoryAccommodationResponse;
import project.member.adapter.in.web.response.ViewHistoryResponse;
import project.member.application.in.query.model.ChatMemberSearchView;
import project.member.application.in.query.model.ChatMembersSearchView;
import project.member.application.in.query.model.DefaultProfileView;
import project.member.application.in.query.model.TripHistoryView;
import project.member.application.in.query.model.ViewHistoryAccommodationView;
import project.member.application.in.query.model.ViewHistoryGroupView;

import java.util.List;

final class MemberQueryResponseMapper {

    private MemberQueryResponseMapper() {
    }

    static DefaultProfileResponse toResponse(DefaultProfileView profile) {
        return new DefaultProfileResponse(
                profile.name(),
                profile.profileImageUrl(),
                profile.createdDate(),
                profile.aboutMe(),
                profile.isEmailVerified()
        );
    }

    static ChatMembersSearchResponse toResponse(ChatMembersSearchView search) {
        List<ChatMemberSearchResponse> members = search.members().stream()
                                                       .map(MemberQueryResponseMapper::toResponse)
                                                       .toList();
        return new ChatMembersSearchResponse(members);
    }

    private static ChatMemberSearchResponse toResponse(ChatMemberSearchView member) {
        return new ChatMemberSearchResponse(
                member.id(),
                member.name(),
                member.createdDateTime(),
                member.profileImageUrl()
        );
    }

    static TripHistoryResponse toResponse(TripHistoryView tripHistory) {
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

    static ViewHistoryResponse toResponse(ViewHistoryGroupView view) {
        return new ViewHistoryResponse(
                view.date(),
                view.accommodations()
                    .stream()
                    .map(MemberQueryResponseMapper::toResponse)
                    .toList()
        );
    }

    private static ViewHistoryAccommodationResponse toResponse(ViewHistoryAccommodationView view) {
        return new ViewHistoryAccommodationResponse(
                view.viewDate(),
                view.accommodationId(),
                view.title(),
                view.avgRate(),
                view.thumbnailUrl(),
                view.isInWishlist(),
                view.wishlistId(),
                view.wishlistName()
        );
    }
}
