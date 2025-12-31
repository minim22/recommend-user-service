package com.custom.recommend_user_service.exception;

import java.time.LocalDateTime;

import com.custom.recommend_user_service.enums.ResultCode;

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
     * ResultCode(ErrorCode 포함)를 기반으로 ErrorResponse 생성
     */
    public static ErrorResponse of(final ResultCode resultCode, final String path) {
        return new ErrorResponse(
            resultCode.getCode(),
            resultCode.getTitle(),
            resultCode.getMessage(),
            resultCode.getStatus(),
            LocalDateTime.now(),
            path
        );
    }

    /**
     * 메시지를 커스텀하고 싶을 때 사용하는 생성 메서드
     */
    public static ErrorResponse of(final ResultCode resultCode, final String customMessage, final String path) {
        return new ErrorResponse(
            resultCode.getCode(),
            resultCode.getTitle(),
            customMessage,
            resultCode.getStatus(),
            LocalDateTime.now(),
            path
        );
    }
}