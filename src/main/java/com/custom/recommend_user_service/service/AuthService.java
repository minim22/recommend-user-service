package com.custom.recommend_user_service.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.custom.recommend_user_service.dto.request.LoginRequest;
import com.custom.recommend_user_service.dto.response.LoginResponse;
import com.custom.recommend_user_service.dto.response.TokenResponse;
import com.custom.recommend_user_service.entity.User;
import com.custom.recommend_user_service.enums.ErrorCode;
import com.custom.recommend_user_service.exception.ApiException;
import com.custom.recommend_user_service.repository.LoginRepository;
import com.custom.recommend_user_service.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 로그인 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final LoginRepository loginRepository;
    private final JwtTokenProvider jwtTokenProvider;
    
    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final int ACCOUNT_LOCK_HOURS = 1;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 로그인 요청
     * @param request
     * @return
     */
    public LoginResponse login(LoginRequest request){

        log.info("[LoginService] 로그인 시도: email={}", request.email());

        // 1. 사용자 인증
        User user = loginRepository.findUserByEmail(request.email());
        if(user == null){
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }
        // 2. 패스워드 검증
        verifyPassword(user, request.password());

        // 3. 계정상태 검증
        validateAccountStatus(user);

        // 4. 로그인 성공 처리
        user.loginSuccess();

        // 5. 토큰 발급
        // accessToken + refreshToken
        TokenResponse tokenResponse = jwtTokenProvider.createToken(user);

        log.info("[LoginService] 로그인 성공: userId={}, email={}", user.getId(), maskEmail(user.getEmail()));

        // 6. 결과
        return LoginResponse.of(
            user.getEmail(),
            user.getName(),
            user.getProfileImageUrl(),
            user.getRole(),
            tokenResponse.accessToken(),
            tokenResponse.refreshToken(),
            tokenResponse.accessTokenExpiresIn(),
            tokenResponse.refreshTokenExpiresIn()
        );
    }

    /**
     * 계정 상태 검증
     */
    private void validateAccountStatus(User user) {
        // OAuth 사용자 체크
        if (user.isOAuthUser()) {
            log.warn("[LoginService] OAuth 사용자 일반 로그인 시도: userId={}, provider={}", 
                user.getId(), user.getProvider());
            throw new ApiException(ErrorCode.LOGIN_FAILED,
                String.format("%s 계정으로 로그인해주세요.", user.getProvider().name()));
        }

        // 계정 상태 체크
        switch (user.getStatus()) {
            case INACTIVE -> {
                log.warn("[LoginService] 비활성화된 계정: userId={}", user.getId());
                throw new ApiException(ErrorCode.ACCOUNT_INACTIVE, "비활성화된 계정입니다. 관리자에게 문의하세요.");
            }
            case DELETED -> {
                log.warn("[LoginService] 삭제된 계정: userId={}", user.getId());
                throw new ApiException(ErrorCode.ACCOUNT_DELETED, "탈퇴한 계정입니다.");
            }
            case ACTIVE -> { /* 정상 */ }
        }

        // 계정 잠금 체크
        if (user.isAccountLocked()) {
            log.warn("[LoginService] 잠긴 계정: userId={}, lockedUntil={}", 
                user.getId(), user.getAccountLockedUntil());
            throw new ApiException(ErrorCode.ACCOUNT_LOCKED,
                String.format("계정이 잠겼습니다. %s 이후에 다시 시도해주세요.",
                    formatDateTime(user.getAccountLockedUntil())));
        }
    }

    /**
     * 비밀번호 검증
     */
    private void verifyPassword(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            user.loginFailed();

            int remainingAttempts = MAX_LOGIN_FAIL_COUNT - user.getLoginFailCount();

            log.warn("[LoginService] 비밀번호 불일치: userId={}, failCount={}", 
                user.getId(), user.getLoginFailCount());

            if (user.getLoginFailCount() >= MAX_LOGIN_FAIL_COUNT) {
                throw new ApiException(ErrorCode.ACCOUNT_LOCKED);
            }

            throw new ApiException(ErrorCode.LOGIN_FAILED,
                String.format("이메일 또는 비밀번호가 올바르지 않습니다. (남은 시도: %d회)", 
                    Math.max(0, remainingAttempts)));
        }
    }
    
    /**
     * 이메일 마스킹 (로그 보안용)
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        
        return local.length() <= 3
            ? local.charAt(0) + "***@" + domain
            : local.substring(0, 3) + "***@" + domain;
    }

    /**
     * 날짜 포맷팅
     */
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }
}
