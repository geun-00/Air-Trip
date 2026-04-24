package project.member.application.out.query;

import project.member.application.query.model.ChatMembersSearchView;

public interface SearchMembersPort {

    ChatMembersSearchView findMembersByName(String name);
}
