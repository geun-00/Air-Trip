package project.member.application.out.command;

import project.member.domain.Member;
import project.member.domain.SocialType;

public interface LoadMemberPort {

    Member loadById(Long memberId);

    Member loadByEmail(String email);

    String loadMemberName(Long memberId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndSocialType(String email, SocialType socialType);
}
