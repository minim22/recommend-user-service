package com.custom.recommend_user_service.dto.response;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn  // 액세스 토큰 만료 시간 (초)
) {
    /**
     * Bearer 토큰 응답 생성
     */
    public static TokenResponse of(String accessToken, String refreshToken, Long expiresIn) {
        return new TokenResponse(
            accessToken,
            refreshToken,
            "Bearer",
            expiresIn
        );
    }
}