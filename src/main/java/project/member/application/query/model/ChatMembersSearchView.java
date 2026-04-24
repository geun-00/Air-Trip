package project.member.application.query.model;

import java.util.List;

public record ChatMembersSearchView(
        List<ChatMemberSearchView> members
) {
}
