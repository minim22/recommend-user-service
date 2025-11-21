package com.custom.recommend_user_service.enums;

public enum Provider {
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver");

    private final String registrationId;

    Provider(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    /**
     * registrationId로 Provider 찾기
     */
    public static Provider fromRegistrationId(String registrationId) {
        for (Provider provider : values()) {
            if (provider.registrationId.equalsIgnoreCase(registrationId)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown provider: " + registrationId);
    }
}
