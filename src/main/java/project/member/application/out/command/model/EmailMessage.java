package project.member.application.out.command.model;

public record EmailMessage(
        String to,
        String subject,
        String body,
        String replyTo
) {
}

