package project.auth.application.out.command;

import project.auth.application.out.command.model.IssuedAuthTokens;
import project.auth.application.out.command.model.AuthTokenClaims;
import project.member.domain.Member;

public interface AuthTokenPort {

    void validate(String token);

    AuthTokenClaims loadClaims(String token);

    long loadRemainingMillis(String token);

    IssuedAuthTokens issue(Member member, String principalName);
}
