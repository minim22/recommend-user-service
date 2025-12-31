package com.custom.recommend_user_service.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import com.custom.recommend_user_service.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.custom.recommend_user_service.dto.response.TokenResponse;
import com.custom.recommend_user_service.entity.User;
import com.custom.recommend_user_service.enums.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 생성 및 검증
 *
 * @since 1.0
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;

    // 30분 * 60초 * 1000ms = 1,800,000ms
    private long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30;

    // 2. Refresh Token: 3일
    // 3일 * 24시간 * 60분 * 60초 * 1000ms = 259,200,000ms
    private long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 3;

    public JwtTokenProvider(
        @Value("${jwt.secret}") final String secret,
        @Value("${jwt.access-token-validity-in-seconds}") final long accessTokenValidityInSeconds,
        @Value("${jwt.refresh-token-validity-in-seconds}") final long refreshTokenValidityInSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(final Long userId, final String email, final String role) {
        final Instant now = Instant.now();
        final Instant expiration = now.plus(accessTokenValidityInSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("email", email)
            .claim("role", role)
            .claim("type", "access")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(secretKey, Jwts.SIG.HS512)
            .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(final Long userId) {
        final Instant now = Instant.now();
        final Instant expiration = now.plus(refreshTokenValidityInSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("type", "refresh")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(secretKey, Jwts.SIG.HS512)
            .compact();
    }

    /**
     * Accesstoken + Refreshtoken
     * - 알고리즘 : ES256
     * @param user 유저
     * @return
     */
    public TokenResponse createToken(User user){

        // 현재시간
        long now = System.currentTimeMillis();

        // Access Token 생성 및 만료 설정
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .subject(user.getEmail())
                .claim("auth", user.getRole())
                .claim("type", "access")
                .issuedAt(new Date(now))
                .signWith(secretKey)
                .expiration(accessTokenExpiresIn)
                .compact();

        // Refresh Token 생성 및 만료 설정
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
                .issuedAt(new Date(now))
                .expiration(refreshTokenExpiresIn)
                .signWith(secretKey)
                .compact();

        return new TokenResponse(
            "Bearer",
            accessToken,
            refreshToken,
            accessTokenExpiresIn.getTime(),
            refreshTokenExpiresIn.getTime()
        );
    }

    /**
     * 토큰에서 Authentication 추출
     */
    public Authentication getAuthentication(final String token) {
        final Claims claims = parseClaims(token);
        
        final String userId = claims.getSubject();
        final String role = claims.get("role", String.class);
        
        final Collection<? extends GrantedAuthority> authorities = 
            List.of(new SimpleGrantedAuthority(role));
        
        return new UsernamePasswordAuthenticationToken(userId, token, authorities);
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public Long getUserId(final String token) {
        final Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰에서 이메일 추출
     */
    public String getEmail(final String token) {
        final Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(final String token) {
        try {
            parseClaims(token);
            return true;
        } catch (final MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        } catch (final ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw new ApiException(ErrorCode.EXPIRED_TOKEN);
        } catch (final UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        } catch (final IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 토큰 파싱
     */
    private Claims parseClaims(final String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Access Token 만료 시간 반환 (초)
     */
    public long getAccessTokenValidityInSeconds() {
        return accessTokenValidityInSeconds;
    }

    /**
     * Refresh Token 만료 시간 반환 (초)
     */
    public long getRefreshTokenValidityInSeconds() {
        return refreshTokenValidityInSeconds;
    }
}
