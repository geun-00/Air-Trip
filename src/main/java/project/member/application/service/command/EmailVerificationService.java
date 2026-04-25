package project.member.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.member.application.in.command.SendEmailVerificationUseCase;
import project.member.application.in.command.VerifyEmailUseCase;
import project.member.application.out.command.LoadMemberPort;
import project.member.application.out.command.ManageEmailVerificationTokenPort;
import project.member.application.out.command.SaveMemberPort;
import project.member.application.out.command.SendEmailPort;
import project.member.domain.Member;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailVerificationService implements SendEmailVerificationUseCase, VerifyEmailUseCase {

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;

    private final SendEmailPort sendEmailPort;
    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;
    private final ManageEmailVerificationTokenPort manageEmailVerificationTokenPort;

    @Async
    @Retryable(retryFor = MailSendException.class, backoff = @Backoff(delay = 1000))
    @Override
    public void sendEmail(Long memberId) {
        Member member = loadMemberPort.loadById(memberId);

        String token = UUID.randomUUID().toString();
        String link = buildVerificationLink(token);
        String subject = "[Air-Trip] 이메일 인증을 완료해주세요.";
        String html = generateHtml(link);
        String email = member.getEmail();

        int retryCount = RetrySynchronizationManager.getContext().getRetryCount();
        log.debug("이메일 인증 링크 전송: {}, 시도 횟수 {}", email, retryCount);

        sendEmailPort.sendHtml(email, subject, html, "no-reply@air-trip.com");
        manageEmailVerificationTokenPort.save(token, memberId);

        log.debug("이메일 인증 링크 전송 성공: {}", email);
    }

    @Recover
    public void recoverSendEmail(MailException ex) {
        log.error("[메일 전송 실패]", ex);
    }

    @Transactional
    @Override
    public String verifyToken(String token) {
        Long memberId = manageEmailVerificationTokenPort.findMemberIdByToken(token);

        if (memberId == null) {
            return buildVerificationRedirectUrl(false);
        }

        Member member = loadMemberPort.loadById(memberId);
        member.verifyEmail();
        saveMemberPort.save(member);
        manageEmailVerificationTokenPort.deleteByToken(token);

        return buildVerificationRedirectUrl(true);
    }

    private String buildVerificationLink(String token) {
        return baseUrl + "/api/auth/email/verify?token=" + token;
    }

    private String buildVerificationRedirectUrl(boolean success) {
        return frontendUrl + "/users/profile?emailVerify=" + (success ? "success" : "failed");
    }

    private String generateHtml(String link) {
        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f7f7f7;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 30px auto;
                            background: #ffffff;
                            border-radius: 10px;
                            overflow: hidden;
                            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
                        }
                        .header {
                            background-color: #FF5A5F;
                            color: white;
                            padding: 20px;
                            text-align: center;
                            font-size: 24px;
                            font-weight: bold;
                        }
                        .content {
                            padding: 30px;
                            color: #333;
                        }
                        .button {
                            display: inline-block;
                            padding: 12px 24px;
                            margin: 20px 0;
                            background-color: #FF5A5F;
                            color: #fff !important;
                            text-decoration: none;
                            border-radius: 6px;
                            font-size: 16px;
                        }
                        .footer {
                            padding: 20px;
                            font-size: 12px;
                            text-align: center;
                            color: #888;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">Air-Trip</div>
                        <div class="content">
                            <p>안녕하세요,</p>
                            <p>인증을 완료하시려면 아래 버튼을 클릭해 인증을 진행해주세요.</p>
                            <p style="text-align: center;">
                                <a href="%s" class="button">인증하기</a>
                            </p>
                            <p style="color:#555; font-size:13px;">※ 본 메일은 발신 전용으로 회신이 불가합니다.</p>
                        </div>
                        <div class="footer">
                            &copy; 2026 Air-Trip. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(link);
    }
}
