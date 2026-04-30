package project.auth.config.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import project.auth.application.out.command.ManageBlacklistedTokenPort;
import project.auth.domain.exception.JwtProcessingException;
import project.common.exception.ErrorCode;
import project.infrastructure.jwt.JwtProvider;

import java.io.IOException;

import static project.infrastructure.jwt.JwtProperties.ACCESS_TOKEN_KEY;
import static project.infrastructure.jwt.JwtProperties.AUTHORIZATION_HEADER;
import static project.infrastructure.jwt.JwtProperties.TOKEN_PREFIX;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationResolver jwtAuthenticationResolver;
    private final ManageBlacklistedTokenPort manageBlacklistedTokenPort;

    /**
     * @throws CredentialsExpiredException         token has expired
     * @throws InsufficientAuthenticationException token is invalid
     * @throws JwtException                        cause is EntityNotFoundException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);

        if (accessToken != null) {
            if (manageBlacklistedTokenPort.contains(accessToken)) {
                throw new JwtProcessingException(ErrorCode.BLACKLISTED_TOKEN);
            }

            jwtProvider.validateToken(accessToken);
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationResolver.resolve(accessToken));
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return PatternMatchUtils.simpleMatch("/api/auth/logout", requestURI);
    }

    private String resolveToken(HttpServletRequest request) {
        // 1. 헤더에서 토큰 확인 (기존 방식 유지)
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }

        // 2. 쿠키에서 토큰 확인 (SSE 지원)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(ACCESS_TOKEN_KEY)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
