package project.auth.domain.exception;

import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;

public class AuthException extends BusinessException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
