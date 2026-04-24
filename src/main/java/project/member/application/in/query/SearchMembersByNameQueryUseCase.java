package project.member.application.in.query;

import project.member.adapter.in.web.response.ChatMembersSearchResponse;

public interface SearchMembersByNameQueryUseCase {

    ChatMembersSearchResponse findMembersByName(String name);
}
