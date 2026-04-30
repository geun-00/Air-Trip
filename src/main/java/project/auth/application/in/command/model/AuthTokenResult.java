package project.auth.application.in.command.model;

public record AuthTokenResult(
        String accessToken,
        String refreshToken,
        long accessTokenTtlSeconds,
        long refreshTokenTtlSeconds
) {
}
