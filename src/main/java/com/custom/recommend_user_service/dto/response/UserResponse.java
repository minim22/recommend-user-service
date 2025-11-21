package com.custom.recommend_user_service.dto.response;

import java.time.LocalDateTime;

import com.custom.recommend_user_service.entity.User;

/**
 * 사용자 정보 응답 DTO
 */
public record UserResponse(
    Long id,
    String email,
    String name,
    String profileImageUrl,
    String role,
    String status,
    String provider,  // null이면 일반 회원가입
    LocalDateTime createdAt,
    LocalDateTime lastLoginAt
) {
    /**
     * Entity -> DTO 변환
     */
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getProfileImageUrl(),
            user.getRole() != null ? user.getRole().name() : null,
            user.getStatus() != null ? user.getStatus().name() : null,
            user.getProvider() != null ? user.getProvider().name() : null,
            user.getCreatedAt(),
            user.getLastLoginAt()
        );
    }

    /**
     * 일반 회원가입 사용자인지 확인
     */
    public boolean isLocalUser() {
        return provider == null;
    }

    /**
     * OAuth 사용자인지 확인
     */
    public boolean isOAuthUser() {
        return provider != null;
    }
}
