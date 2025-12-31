package com.custom.recommend_user_service.exception;


import com.custom.recommend_user_service.enums.ResultCode;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final com.custom.recommend_user_service.enums.ResultCode resultCode;
    private final String customMessage;

    /**
     * 기본 응답값
     * @param resultCode
     */
    public ApiException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.customMessage = null;
    }

    /**
     * 커스텀 메시지 생성자 - 동적 메시지 사용
     */
    public ApiException(ResultCode resultCode, String customMessage) {
        super(customMessage);
        this.resultCode = resultCode;
        this.customMessage = customMessage;
    }

    /**
     * 원인 예외 포함 생성자
     */
    public ApiException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.resultCode = resultCode;
        this.customMessage = null;
    }

    /**
     * 커스텀 메시지 + 원인 예외 생성자
     */
    public ApiException(ResultCode resultCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.resultCode = resultCode;
        this.customMessage = customMessage;
    }

    /**
     * 응답에 사용할 메시지 반환
     */
    public String getResponseMessage() {
        return customMessage != null ? customMessage : resultCode.getMessage();
    }
}