package project.common.adapter.in.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import project.common.adapter.in.web.ErrorResponse.FieldErrorResponse;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.auth.adapter.out.redis.RedisRepository;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    private final RedisRepository redisRepository;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleEmailExistsException(BusinessException e) {
        log.warn("API 예외 발생: {}", e.getMessage());

        ErrorCode errorCode = e.getErrorCode();
        return new ResponseEntity<>(ErrorResponse.from(errorCode), errorCode.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        List<FieldErrorResponse> fieldErrors = ex.getBindingResult()
                                                 .getFieldErrors()
                                                 .stream()
                                                 .map(fe -> new FieldErrorResponse(
                                                         fe.getField(),
                                                         fe.getRejectedValue(),
                                                         fe.getDefaultMessage()
                                                 ))
                                                 .toList();

        ErrorResponse errorResponse = ErrorResponse.withFieldError(fieldErrors);
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleMailSendException(MailException e) {
        e.printStackTrace(System.err);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.from(errorCode);

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }
}
