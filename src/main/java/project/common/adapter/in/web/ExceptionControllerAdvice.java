package project.common.adapter.in.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ProblemDetail;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import project.auth.exception.JwtProcessingException;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handle(BusinessException e) {
        log.warn("API 예외 발생: {}", e.getMessage());

        ErrorCode errorCode = e.getErrorCode();
        return new ResponseEntity<>(createProblemDetail(errorCode), errorCode.getHttpStatus());
    }

    @ExceptionHandler(JwtProcessingException.class)
    public ResponseEntity<ProblemDetail> handle(JwtProcessingException e) {
        log.warn("JWT 예외 발생: {}", e.getMessage());

        ErrorCode errorCode = e.getErrorCode();
        return new ResponseEntity<>(createProblemDetail(errorCode), errorCode.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult()
                                               .getFieldErrors()
                                               .stream()
                                               .map(fe -> new FieldErrorDetail(
                                                       fe.getField(),
                                                       fe.getRejectedValue(),
                                                       fe.getDefaultMessage()
                                               ))
                                               .toList();

        ProblemDetail problemDetail = createProblemDetail(ErrorCode.INVALID_INPUT);
        problemDetail.setProperty("errors", fieldErrors);

        return new ResponseEntity<>(problemDetail, headers, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handle(Exception e) {
        log.error("처리되지 않은 예외 발생", e);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(createProblemDetail(errorCode), errorCode.getHttpStatus());
    }

    private ProblemDetail createProblemDetail(ErrorCode errorCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                errorCode.getHttpStatus(),
                errorCode.getMessage()
        );
        problemDetail.setTitle(errorCode.name());
        problemDetail.setProperty("errorCode", errorCode.getCode());

        return problemDetail;
    }

    private record FieldErrorDetail(String field, Object rejectedValue, String message) {}
}
