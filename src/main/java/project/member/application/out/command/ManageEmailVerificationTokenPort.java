package project.member.application.out.command;

public interface ManageEmailVerificationTokenPort {

    void save(String token, Long memberId);

    Long findMemberIdByToken(String token);

    void deleteByToken(String token);
}
