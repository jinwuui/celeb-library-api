package com.eunbinlib.api.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserMeRes {

    private final String userType;

    private final Long id;
    private final String username;
    private final String imageUrl;

    @Builder
    public UserMeRes(String userType, Long id, String username, String imageUrl) {
        this.userType = userType;
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
    }
}
