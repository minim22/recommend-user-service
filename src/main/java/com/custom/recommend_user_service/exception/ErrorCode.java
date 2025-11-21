package com.custom.recommend_user_service.exception;

import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 *
 * <p>코드 체계: E-{카테고리}{일련번호}
 * <ul>
 *   <li>C (Common): 공통 예외</li>
 *   <li>A (Auth): 인증/인가</li>
 *   <li>U (User): 사용자 관련</li>
 *   <li>O (OAuth): OAuth 관련</li>
 * </ul>
 *
 * @since 1.0
 */
public enum ErrorCode {

    // ==================== Common (공통) ====================
    INVALID_INPUT_VALUE(
        HttpStatus.BAD_REQUEST,
        "E-C001",
        "잘못된 입력값",
        "입력값이 올바르지 않습니다."
    ),
    INVALID_TYPE_VALUE(
        HttpStatus.BAD_REQUEST,
        "E-C002",
        "잘못된 타입",
        "요청 데이터의 타입이 올바르지 않습니다."
    ),
    RESOURCE_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "E-C003",
        "리소스 없음",
        "요청한 리소스를 찾을 수 없습니다."
    ),
    METHOD_NOT_ALLOWED(
        HttpStatus.METHOD_NOT_ALLOWED,
        "E-C004",
        "허용되지 않은 메서드",
        "지원하지 않는 HTTP 메서드입니다."
    ),
    INTERNAL_SERVER_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "E-C999",
        "서버 오류",
        "서버 내부 오류가 발생했습니다."
    ),

    // ==================== Auth (인증/인가) ====================
    UNAUTHORIZED(
        HttpStatus.UNAUTHORIZED,
        "E-A001",
        "인증 필요",
        "인증이 필요합니다."
    ),
    FORBIDDEN(
        HttpStatus.FORBIDDEN,
        "E-A002",
        "접근 권한 없음",
        "해당 리소스에 접근할 권한이 없습니다."
    ),
    INVALID_TOKEN(
        HttpStatus.UNAUTHORIZED,
        "E-A003",
        "유효하지 않은 토큰",
        "토큰이 유효하지 않습니다."
    ),
    EXPIRED_TOKEN(
        HttpStatus.UNAUTHORIZED, 
        "E-A004", 
        "만료된 토큰", 
        "토큰이 만료되었습니다."
    ),
    INVALID_REFRESH_TOKEN(
        HttpStatus.UNAUTHORIZED, 
        "E-A005", 
        "유효하지 않은 리프레시 토큰", 
        "리프레시 토큰이 유효하지 않습니다."
    ),

    // ==================== User (사용자) ====================
    USER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "E-U001",
        "사용자 없음",
        "사용자를 찾을 수 없습니다."
    ),
    EMAIL_ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "E-U002",
        "이메일 중복",
        "이미 사용 중인 이메일입니다."
    ),
    INVALID_CREDENTIALS(
        HttpStatus.UNAUTHORIZED,
        "E-U003",
        "인증 정보 불일치",
        "이메일 또는 비밀번호가 올바르지 않습니다."
    ),
    PASSWORD_NOT_MATCH(
        HttpStatus.BAD_REQUEST,
        "E-U004",
        "비밀번호 불일치",
        "비밀번호가 일치하지 않습니다."
    ),
    ACCOUNT_LOCKED(
        HttpStatus.UNAUTHORIZED,
        "E-U005",
        "계정 잠김",
        "로그인 실패 횟수 초과로 계정이 잠겼습니다."
    ),
    INACTIVE_USER(
        HttpStatus.FORBIDDEN, 
        "E-U006", 
        "비활성 계정", 
        "비활성화된 계정입니다."
    ),
    DELETED_USER(
        HttpStatus.GONE, 
        "E-U007", 
        "삭제된 계정", 
        "삭제된 계정입니다."
    ),

    // ==================== OAuth ====================
    OAUTH_PROVIDER_NOT_SUPPORTED(
        HttpStatus.BAD_REQUEST,
        "E-O001",
        "지원하지 않는 OAuth 제공자",
        "지원하지 않는 OAuth 제공자입니다."
    ),
    OAUTH_USER_INFO_ERROR(
        HttpStatus.BAD_REQUEST,
        "E-O002",
        "OAuth 사용자 정보 오류",
        "OAuth 제공자로부터 사용자 정보를 가져올 수 없습니다."
    ),
    OAUTH_AUTHENTICATION_FAILED(
        HttpStatus.UNAUTHORIZED,
        "E-O003",
        "OAuth 인증 실패",
        "OAuth 인증에 실패했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String title;
    private final String message;

    ErrorCode(
        final HttpStatus status,
        final String code,
        final String title,
        final String message
    ) {
        this.status = status;
        this.code = code;
        this.title = title;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getStatusCode() {
        return status.value();
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}



