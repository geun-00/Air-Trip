package project.member.application.in.command;

public interface SendEmailVerificationUseCase {

    void sendEmail(Long memberId);
}
