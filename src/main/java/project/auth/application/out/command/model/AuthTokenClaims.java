package project.auth.application.out.command.model;

public record AuthTokenClaims(
        Long memberId,
        String principalName
) {
}
