package com.custom.recommend_user_service.security.hadler;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.custom.recommend_user_service.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private ObjectMapper objectMapper;

    @Override
    public void handle(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final AccessDeniedException accessDeniedException
    ) throws IOException, JsonProcessingException, IOException {
        
        log.warn("Forbidden request: path={}, message={}",
            request.getRequestURI(), 
            accessDeniedException.getMessage()
        );

        final ErrorCode errorCode = ErrorCode.FORBIDDEN;

        response.setStatus(errorCode.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        final Map<String, Object> errorResponse = Map.of(
            "code", errorCode.getCode(),
            "title", errorCode.getTitle(),
            "message", errorCode.getMessage(),
            "status", errorCode.getStatusCode(),
            "path", request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}