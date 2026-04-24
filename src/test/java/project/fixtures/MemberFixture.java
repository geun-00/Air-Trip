package project.fixtures;

import project.member.domain.Member;
import project.member.domain.support.RestMemberCreateSpec;

import java.util.UUID;

public class MemberFixture {

    public static Member create() {
        return Member.createForRest(new RestMemberCreateSpec("test-user", "test@email.com", null, null, UUID.randomUUID().toString()));
    }
}
