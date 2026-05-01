package project.member.application.in.query;

import project.member.application.in.query.model.ChatMembersSearchView;

public interface SearchMembersUseCase {

    ChatMembersSearchView findMembersByName(String name);
}
