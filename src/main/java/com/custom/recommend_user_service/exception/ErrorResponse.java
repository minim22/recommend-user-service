package com.custom.recommend_user_service.exception;

import java.time.LocalDateTime;

/**
 * 에러 응답 DTO
 * 
 * <p>RFC 7807 (Problem Details for HTTP APIs) 표준을 참고한 구조
 * 
 * @param code 에러 코드 (예: E-U001)
 * @param title 에러 제목 (간단한 설명)
 * @param message 에러 메시지 (상세 설명)
 * @param status HTTP 상태 코드
 * @param timestamp 발생 시각
 * @param path 요청 경로
 *
 * @since 1.0
 */
public record ErrorResponse(
    String code,
    String title,
    String message,
    Integer status,
    LocalDateTime timestamp,
    String path
) {
    /**
     * ErrorCode로부터 ErrorResponse 생성
     */
    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(
            errorCode.getCode(),
            errorCode.getTitle(),
            errorCode.getMessage(),
            errorCode.getStatusCode(),
            LocalDateTime.now(),
            null
        );
    }

    /**
     * ErrorCode와 경로로 ErrorResponse 생성
     */
    public static ErrorResponse of(final ErrorCode errorCode, final String path) {
        return new ErrorResponse(
            errorCode.getCode(),
            errorCode.getTitle(),
            errorCode.getMessage(),
            errorCode.getStatusCode(),
            LocalDateTime.now(),
            path
        );
    }

    /**
     * 커스텀 메시지로 ErrorResponse 생성
     */
    public static ErrorResponse of(
        final ErrorCode errorCode,
        final String customMessage,
        final String path
    ) {
        return new ErrorResponse(
            errorCode.getCode(),
            errorCode.getTitle(),
            customMessage,
            errorCode.getStatusCode(),
            LocalDateTime.now(),
            path
        );
    }

    /**
     * 완전 커스텀 ErrorResponse 생성 (validation 등)
     */
    public static ErrorResponse of(
        final String code,
        final String title,
        final String message,
        final Integer status,
        final String path
    ) {
        return new ErrorResponse(
            code,
            title,
            message,
            status,
            LocalDateTime.now(),
            path
        );
    }
}



