package project.member.application.out.command;

import project.member.application.out.command.model.EmailMessage;

public interface SendEmailPort {

    void send(EmailMessage message);
}
