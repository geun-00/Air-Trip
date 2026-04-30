package project.auth.application.in.command.model;

public record LoginCommand(
        String email,
        String password
) {
}
