package project.auth.domain.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import project.common.exception.ErrorCode;

@Getter
public class CustomAuthenticationException extends AuthenticationException {
    private final ErrorCode errorCode;

    public CustomAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
