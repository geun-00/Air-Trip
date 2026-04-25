package project.member.application.in.query;

import project.member.application.in.query.model.ChatMembersSearchView;

public interface SearchMembersByNameQueryUseCase {

    ChatMembersSearchView findMembersByName(String name);
}
