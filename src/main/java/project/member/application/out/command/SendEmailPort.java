package project.member.application.out.command;

public interface SendEmailPort {

    void sendHtml(String to, String subject, String html, String replyTo);
}
