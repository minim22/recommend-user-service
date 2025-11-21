package com.custom.recommend_user_service.security.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.custom.recommend_user_service.exception.CustomException;
import com.custom.recommend_user_service.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private JwtTokenProvider jwtTokenProvider;
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            // 1. Request Header에서 JWT 토큰 추출
            final String token = resolveToken(request);

            // 2. 토큰 유효성 검증
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 3. 토큰에서 Authentication 추출
                final Authentication authentication = jwtTokenProvider.getAuthentication(token);
                
                // 4. SecurityContext에 Authentication 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Set Authentication to SecurityContext: userId={}", 
                    authentication.getName());
            }

            filterChain.doFilter(request, response);
            
        } catch (final CustomException e) {
            log.warn("JWT authentication failed: code={}, message={}", 
                e.getErrorCode().getCode(), 
                e.getMessage()
            );
            setErrorResponse(response, e);
        }
    }

    /**
     * Request Header에서 토큰 추출
     */
    private String resolveToken(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }

    /**
     * 인증 실패 응답 설정
     */
    private void setErrorResponse(
        final HttpServletResponse response,
        final CustomException e
    ) throws IOException {
        response.setStatus(e.getErrorCode().getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        final Map<String, Object> errorResponse = Map.of(
            "code", e.getErrorCode().getCode(),
            "title", e.getErrorCode().getTitle(),
            "message", e.getErrorCode().getMessage(),
            "status", e.getErrorCode().getStatusCode()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}