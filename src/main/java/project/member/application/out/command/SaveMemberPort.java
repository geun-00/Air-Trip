package project.member.application.out.command;

import project.member.domain.Member;

public interface SaveMemberPort {

    Member save(Member member);
}
