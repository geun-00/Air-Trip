package project.member.application.in.command;

public interface ManageEmailVerificationUseCase {

    void sendEmail(Long memberId);

    String verifyToken(String token);
}
