package project.common.adapter.in.web.response;

import project.common.exception.ErrorCode;

public record ErrorCodeResponse(
        int status,
        String message,
        String errorCode
) {

    public static ErrorCodeResponse from(ErrorCode errorCode) {
        return new ErrorCodeResponse(
                errorCode.getHttpStatus().value(),
                errorCode.getMessage(),
                errorCode.getCode()
        );
    }
}
