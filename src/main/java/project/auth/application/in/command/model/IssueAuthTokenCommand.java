package project.auth.application.in.command.model;

public record IssueAuthTokenCommand(String email, String principalName) {
}
