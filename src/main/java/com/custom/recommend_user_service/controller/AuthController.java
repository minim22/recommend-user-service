package com.custom.recommend_user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.custom.recommend_user_service.common.ApiResult;
import com.custom.recommend_user_service.dto.request.LoginRequest;
import com.custom.recommend_user_service.dto.request.SignupRequest;
import com.custom.recommend_user_service.dto.response.LoginResponse;
import com.custom.recommend_user_service.service.AuthService;
import com.custom.recommend_user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "인증 관련 API (로그인, 회원가입 등)")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(
        summary = "로그인",
        description = "사용자 인증 후 Access Token과 Refresh Token을 발급합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "로그인 성공"
    )
    @GetMapping("/login")
    public ResponseEntity<ApiResult<LoginResponse>> login(
        @Parameter(description = "로그인 요청 DTO", required = true)
        @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        
        return ResponseEntity.ok(ApiResult.success(response));
    }

    @Operation(
        summary = "회원가입",
        description = "사용자를 등록합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "로그인 성공"
    )
    @PostMapping("/signup")
    public ResponseEntity<ApiResult<LoginResponse>> signup(
        @Valid @RequestBody SignupRequest request
    ) {
        userService.signup(request);

        return ResponseEntity.ok(ApiResult.success());
    }
}
