package project.auth.adapter.in.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project.common.exception.ErrorCode;
import project.auth.exception.JwtProcessingException;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtProcessingException e) {
            ErrorCode errorCode = e.getErrorCode();
            HttpStatus httpStatus = errorCode.getHttpStatus();

            log.debug("JWT 인증 예외 발생: code={}, message={}, cause={}",
                    errorCode.getCode(), errorCode.getMessage(),
                    e.getCause() != null ? e.getCause().getMessage() : "none");

            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    errorCode.getHttpStatus(),
                    errorCode.getMessage()
            );
            problemDetail.setTitle(errorCode.name());
            problemDetail.setProperty("errorCode", errorCode.getCode());

            response.setStatus(httpStatus.value());
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

            objectMapper.writeValue(response.getWriter(), problemDetail);
        }
    }
}
