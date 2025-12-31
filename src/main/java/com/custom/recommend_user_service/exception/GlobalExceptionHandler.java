package com.custom.recommend_user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.custom.recommend_user_service.enums.ErrorCode;
import com.custom.recommend_user_service.enums.ResultCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 글로벌 예외 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
        final ApiException e,
        final HttpServletRequest request
    ) {
        final ResultCode resultCode = e.getResultCode();
        log.warn("ApiException: code={}, message={}, path={}",
            resultCode.getCode(),
            e.getResponseMessage(),
            request.getRequestURI()
        );

        final ErrorResponse response = ErrorResponse.of(
            resultCode,
            e.getResponseMessage(),
            request.getRequestURI()
        );

        return ResponseEntity
            .status(resultCode.getStatus())
            .body(response);
    }

    /**
     * Validation 예외 처리 (@Valid, @Validated)
     */
    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        BindException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(
        final BindException e,
        final HttpServletRequest request
    ) {
        log.warn("ValidationException: {}", e.getMessage());

        final String errorMessage = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .reduce((msg1, msg2) -> msg1 + ", " + msg2)
            .orElse("입력값이 올바르지 않습니다.");

        final ErrorResponse response = ErrorResponse.of(
            ErrorCode.INVALID_INPUT_VALUE,
            errorMessage,
            request.getRequestURI()
        );

        return ResponseEntity
            .badRequest()
            .body(response);
    }

    /**
     * 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
        final MethodArgumentTypeMismatchException e,
        final HttpServletRequest request
    ) {
        log.warn("TypeMismatchException: parameter={}, value={}, requiredType={}",
            e.getName(),
            e.getValue(),
            e.getRequiredType()
        );

        final String message = String.format(
            "파라미터 '%s'의 값 '%s'는 %s 타입이어야 합니다.",
            e.getName(),
            e.getValue(),
            e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown"
        );

        final ErrorResponse response = ErrorResponse.of(
            ErrorCode.INVALID_TYPE_VALUE,
            message,
            request.getRequestURI()
        );

        return ResponseEntity
            .badRequest()
            .body(response);
    }

    /**
     * 지원하지 않는 HTTP 메서드 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
        final HttpRequestMethodNotSupportedException e,
        final HttpServletRequest request
    ) {
        log.warn("MethodNotSupportedException: method={}, path={}",
            e.getMethod(),
            request.getRequestURI()
        );

        final ErrorResponse response = ErrorResponse.of(
            ErrorCode.METHOD_NOT_ALLOWED,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(response);
    }

    /**
     * 404 Not Found 예외 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
        final NoHandlerFoundException e,
        final HttpServletRequest request
    ) {
        log.warn("NotFoundException: path={}", request.getRequestURI());

        final ErrorResponse response = ErrorResponse.of(
            ErrorCode.RESOURCE_NOT_FOUND,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response);
    }

    /**
     * 처리되지 않은 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
        final Exception e,
        final HttpServletRequest request
    ) {
        log.error("Unexpected exception occurred: path={}", request.getRequestURI(), e);

        final ErrorResponse response = ErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}