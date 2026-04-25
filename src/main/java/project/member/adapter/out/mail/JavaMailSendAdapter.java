package project.member.adapter.out.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import project.member.application.out.command.SendEmailPort;

@Component
@RequiredArgsConstructor
public class JavaMailSendAdapter implements SendEmailPort {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendHtml(String to, String subject, String html, String replyTo) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            helper.setReplyTo(replyTo);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException("메일 전송 과정에서 오류가 발생했습니다, " + e.getMessage(), e);
        }
    }
}
