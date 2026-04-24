package project.auth.domain.exception;

import lombok.Getter;
import project.common.exception.ErrorCode;

@Getter
public class JwtProcessingException extends RuntimeException {
    private final ErrorCode errorCode;

    public JwtProcessingException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public JwtProcessingException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
