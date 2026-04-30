package project.auth.application.out.command.model;

public record IssuedAuthTokens(
        String accessToken,
        String refreshToken,
        long accessTokenTtlSeconds,
        long refreshTokenTtlSeconds
) {
}
