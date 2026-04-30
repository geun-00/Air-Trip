package project.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import project.auth.exception.JwtProcessingException;
import project.common.exception.ErrorCode;
import project.member.domain.Member;
import project.member.domain.Role;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.io.Decoders.BASE64;
import static project.infrastructure.jwt.JwtProperties.PRINCIPAL_NAME;
import static project.infrastructure.jwt.JwtProperties.ROLE;

@Component
public class JwtProvider {

    private final Clock clock;
    private final SecretKey key;
    private final JwtProperties jwtProperties;

    public JwtProvider(Clock clock, JwtProperties jwtProperties) {
        this.clock = clock;
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(BASE64.decode(jwtProperties.getSecretKey()));
    }

    public String generateAccessToken(Member member, String principalName) {
        return generateToken(member.getId(), jwtProperties.getAccessToken().getExpiration(), principalName, member.getRole());
    }

    public String generateRefreshToken(Member member, String principalName) {
        return generateToken(member.getId(), jwtProperties.getRefreshToken().getExpiration(), principalName, member.getRole());
    }

    public void validateToken(String token) {
        parseValidClaims(token);
    }

    public JwtClaims getClaims(String token) {
        return toJwtClaims(parseValidClaims(token));
    }

    public long getRemainingMillis(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.getTime() - clock.millis();
        }
        catch (ExpiredJwtException ignored) {
            return 0;
        }
        catch (MalformedJwtException e) {
            throw new JwtProcessingException(ErrorCode.MALFORMED_TOKEN, e);
        }
        catch (IllegalArgumentException | JwtException e) {
            throw new JwtProcessingException(ErrorCode.INVALID_TOKEN, e);
        }
    }

    private Claims parseValidClaims(String token) {
        try {
            return parseClaims(token);
        }
        catch (ExpiredJwtException e) {
            throw new JwtProcessingException(ErrorCode.TOKEN_EXPIRED, e);
        }
        catch (MalformedJwtException e) {
            throw new JwtProcessingException(ErrorCode.MALFORMED_TOKEN, e);
        }
        catch (IllegalArgumentException | JwtException e) {
            throw new JwtProcessingException(ErrorCode.INVALID_TOKEN, e);
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                   .verifyWith(key)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    private JwtClaims toJwtClaims(Claims claims) {
        try {
            return new JwtClaims(
                    Long.valueOf(claims.getSubject()),
                    claims.get(PRINCIPAL_NAME, String.class),
                    Role.valueOf(claims.get(ROLE, String.class))
            );
        }
        catch (IllegalArgumentException | NullPointerException e) {
            throw new JwtProcessingException(ErrorCode.INVALID_TOKEN, e);
        }
    }

    private String generateToken(Long id, int expiration, String principalName, Role role) {
        Instant now = clock.instant();
        Date issuedAt = Date.from(now);
        Date expiresAt = Date.from(now.plusSeconds(expiration));

        Claims claims = Jwts.claims()
                            .id(UUID.randomUUID().toString())
                            .subject(String.valueOf(id))
                            .add(PRINCIPAL_NAME, principalName) //로그아웃, 연결 끊기 요청에 사용될 사용자 식별값
                            .add(ROLE, role.name())
                            .build();

        return Jwts.builder()
                   .claims(claims)
                   .issuedAt(issuedAt)
                   .expiration(expiresAt)
                   .signWith(key)
                   .compact();
    }
}
