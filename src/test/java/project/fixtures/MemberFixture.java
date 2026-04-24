package project.fixtures;

import project.member.domain.Member;

import java.util.UUID;

public class MemberFixture {

    public static Member create() {
        return Member.createForRest("test-user", "test@email.com", null, null, UUID.randomUUID().toString());
    }
}
