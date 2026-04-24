package project.auth.domain.response;

public record TokenResponse(
        String accessToken,
        String refreshToken)
{
}
