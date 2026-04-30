package project.auth.application.in.command.model;

public record LogoutCommand(String accessToken, String refreshToken) {
}
