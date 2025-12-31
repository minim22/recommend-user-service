package com.custom.recommend_user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.custom.recommend_user_service.dto.request.SignupRequest;
import com.custom.recommend_user_service.entity.User;
import com.custom.recommend_user_service.enums.ErrorCode;
import com.custom.recommend_user_service.enums.UserStatus;
import com.custom.recommend_user_service.exception.ApiException;
import com.custom.recommend_user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회원가입 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 요청
     * @param request 회원 요청정보
     */
    @Transactional
    public void signup(SignupRequest request) {
        
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 2. 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(request.password());
    
        // 3. 회원 엔티티 생성
        User user = User.builder()
            .email(request.getEmail())
            .username(request.getUsername())
            .password(encodedPassword)
            .name(request.getName())
            .phoneNumber(request.getPhoneNumber())
            .role(UserRole.USER)  // 기본 권한
            .status(UserStatus.ACTIVE)
            .build();
        
        // 5. 회원 저장
        userRepository.save(user);


    }
}
