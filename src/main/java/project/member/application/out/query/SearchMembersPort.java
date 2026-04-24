package project.member.application.out.query;

import project.member.adapter.in.web.response.ChatMemberSearchResponse;

import java.util.List;

public interface SearchMembersPort {

    List<ChatMemberSearchResponse> findMembersByName(String name);
}
