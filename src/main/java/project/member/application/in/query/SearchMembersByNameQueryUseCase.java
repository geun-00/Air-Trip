package project.member.application.in.query;

import project.member.application.query.model.ChatMembersSearchView;

public interface SearchMembersByNameQueryUseCase {

    ChatMembersSearchView findMembersByName(String name);
}
