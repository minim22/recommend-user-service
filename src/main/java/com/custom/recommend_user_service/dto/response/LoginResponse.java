package com.custom.recommend_user_service.dto.response;

import com.custom.recommend_user_service.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 응답 DTO
 */
@Schema(description = "로그인 정보")
public record LoginResponse(

    @Schema(description = "email", example = "test@example.co.kr")
    String email,
    
    @Schema(description = "유저명", example = "김리나")
    String name,
    
    @Schema(description = "유저프로필 이미지", example = "images://profile.png")
    String profileImageUrl,
    
    @Schema(description = "사용자 권한", example = "ROLE_USER")
    Role role,

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String refreshToken,

    @Schema(description = "액세스 토큰 만료 시간 (초 단위)", example = "3600")
    Long accessTokenExpiresIn,

    @Schema(description = "리프레시 토큰 만료 시간 (초 단위)", example = "7200")
    Long refreshTokenExpiresIn
) {
    public static LoginResponse of(
        String email,
        String name,
        String profileImageUrl,
        Role role,
        String accessToken,
        String refreshToken,
        Long accessTokenExpiresIn,
        Long refreshTokenExpiresIn
    ) {
        return new LoginResponse(
            email,
            name,
            profileImageUrl,
            role,
            accessToken,
            refreshToken,
            accessTokenExpiresIn,
            refreshTokenExpiresIn
        );
    }
}