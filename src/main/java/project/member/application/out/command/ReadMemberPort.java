package project.member.application.out.command;

import project.member.domain.Member;
import project.member.domain.SocialType;

public interface ReadMemberPort {

    Member getById(Long memberId);

    Member getByEmail(String email);

    String getNameById(Long memberId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndSocialType(String email, SocialType socialType);
}
