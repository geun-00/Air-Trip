package project.member.application.out.command;

import java.util.Optional;

public interface ManageEmailVerificationTokenPort {

    void save(String token, Long memberId);

    Optional<Long> findMemberIdByToken(String token);

    void deleteByToken(String token);
}
