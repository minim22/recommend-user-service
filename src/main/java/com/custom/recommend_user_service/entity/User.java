package com.custom.recommend_user_service.entity;

import java.time.LocalDateTime;

import com.custom.recommend_user_service.enums.Provider;
import com.custom.recommend_user_service.enums.Role;
import com.custom.recommend_user_service.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === OAuth 정보 (소셜 로그인용) ===
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Provider provider;  // null이면 일반 회원가입

    @Column(unique = true)
    private String providerId;  // OAuth 고유 ID

    // === 기본 정보 ===
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String password;  // OAuth 사용자는 null

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 500)
    private String profileImageUrl;

    // === 권한 ===
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    // === 계정 상태 ===
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // === 보안 관련 (일반 로그인용) ===
    @Column(nullable = false)
    private Integer loginFailCount;

    @Column
    private LocalDateTime accountLockedUntil;

    @Column
    private LocalDateTime passwordChangedAt;

    // === 타임스탬프 ===
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastLoginAt;

    // === 정적 팩토리 메서드 ===

    /**
     * 일반 회원가입 사용자 생성
     */
    public static User createLocalUser(
            String email,
            String encodedPassword,
            String name
    ) {
        User user = new User();
        user.provider = null;  // 일반 회원가입 표시
        user.providerId = null;
        user.email = email;
        user.password = encodedPassword;
        user.name = name;
        user.role = Role.USER;
        user.status = UserStatus.ACTIVE;
        user.loginFailCount = 0;
        user.passwordChangedAt = LocalDateTime.now();
        user.createdAt = LocalDateTime.now();
        return user;
    }

    /**
     * OAuth 소셜 로그인 사용자 생성
     */
    public static User createOAuthUser(
            Provider provider,
            String providerId,
            String email,
            String name,
            String profileImageUrl
    ) {
        User user = new User();
        user.provider = provider;
        user.providerId = providerId;
        user.email = email;
        user.password = null;  // OAuth는 비밀번호 없음
        user.name = name;
        user.profileImageUrl = profileImageUrl;
        user.role = Role.USER;
        user.status = UserStatus.ACTIVE;
        user.loginFailCount = 0;
        user.createdAt = LocalDateTime.now();
        return user;
    }

    // === 비즈니스 메서드 ===

    /**
     * 로그인 성공 처리
     */
    public void loginSuccess() {
        this.loginFailCount = 0;
        this.accountLockedUntil = null;
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 로그인 실패 처리
     */
    public void loginFailed() {
        this.loginFailCount++;
        if (this.loginFailCount >= 5) {
            // 5회 실패시 1시간 잠금
            this.accountLockedUntil = LocalDateTime.now().plusHours(1);
        }
    }

    /**
     * 계정 잠금 여부 확인
     */
    public boolean isAccountLocked() {
        if (this.accountLockedUntil == null) {
            return false;
        }
        
        // 잠금 시간이 지났으면 자동 해제
        if (LocalDateTime.now().isAfter(this.accountLockedUntil)) {
            this.accountLockedUntil = null;
            this.loginFailCount = 0;
            return false;
        }
        
        return true;
    }

    /**
     * 로그인 가능 여부 확인
     */
    public boolean canLogin() {
        return this.status == UserStatus.ACTIVE && !isAccountLocked();
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
        this.passwordChangedAt = LocalDateTime.now();
        this.loginFailCount = 0;
    }

    /**
     * 프로필 업데이트
     */
    public void updateProfile(String name, String profileImageUrl) {
        this.name = name;
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    /**
     * 계정 상태 변경
     */
    public void changeStatus(UserStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * 일반 회원가입 사용자인지 확인
     */
    public boolean isLocalUser() {
        return this.provider == null;
    }

    /**
     * OAuth 사용자인지 확인
     */
    public boolean isOAuthUser() {
        return this.provider != null;
    }
}
