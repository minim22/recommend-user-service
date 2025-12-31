package com.custom.recommend_user_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT token조회 정보")
public record TokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long accessTokenExpiresIn,
    Long refreshTokenExpiresIn
) {
    /**
     * Bearer 토큰 응답 생성
     */
    public static TokenResponse of(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long accessTokenExpiresIn,
        Long refreshTokenExpiresIn
    ) {
        return new TokenResponse(
            accessToken,
            refreshToken,
            "Bearer",
            accessTokenExpiresIn,
            refreshTokenExpiresIn
        );
    }
}