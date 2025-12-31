package com.custom.recommend_user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.custom.recommend_user_service.entity.User;
import com.custom.recommend_user_service.enums.Provider;

public interface UserRepository extends JpaRepository<User, Long> {
    // 일반 로그인용
    Optional<User> findByEmailAndProviderIsNull(String email);
    
    // OAuth 로그인용
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
    
    // 이메일 중복 체크 (전체)
    boolean existsByEmail(String email);

    /**
     * 이메일 중복 체크 (일반 회원가입용 - OAuth 제외)
     */
    boolean existsByEmailAndProviderIsNull(String email);

    /**
     * OAuth 사용자 존재 여부 확인
     */
    boolean existsByProviderAndProviderId(Provider provider, String providerId);
}