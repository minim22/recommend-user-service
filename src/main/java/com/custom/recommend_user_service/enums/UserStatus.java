package com.custom.recommend_user_service.enums;

public enum UserStatus {
    ACTIVE("활성", "정상적으로 사용 가능한 계정"),
    INACTIVE("비활성", "일시적으로 사용 중지된 계정"),
    DELETED("삭제", "삭제된 계정");

    private final String displayName;
    private final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 활성 상태인지 확인
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
}
