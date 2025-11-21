package com.custom.recommend_user_service.security.oauth2;

public interface OAuth2UserInfo {
    /**
     * OAuth 제공자 (google, kakao, naver)
     */
    String getProviderId();
    
    /**
     * 이메일
     */
    String getEmail();
    
    /**
     * 이름
     */
    String getName();
    
    /**
     * 프로필 이미지 URL
     */
    String getProfileImageUrl();
}
