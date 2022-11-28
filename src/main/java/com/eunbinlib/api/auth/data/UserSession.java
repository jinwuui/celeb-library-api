package com.eunbinlib.api.auth.data;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSession {

    private final Long id;

    private final String userType;

    private final String username;

    @Builder
    public UserSession(Long id, String userType, String username) {
        this.id = id;
        this.userType = userType;
        this.username = username;
    }
}
