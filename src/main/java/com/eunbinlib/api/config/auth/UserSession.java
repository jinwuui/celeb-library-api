package com.eunbinlib.api.config.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSession {

    private final Long id;

    private final String username;

    @Builder
    public UserSession(Long id, String username) {
        this.id = id;
        this.username = username;
    }

}
