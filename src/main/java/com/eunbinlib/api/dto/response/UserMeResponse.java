package com.eunbinlib.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserMeResponse {

    private final String userType;

    private final Long id;
    private final String username;
    private final String imageUrl;

    @Builder
    public UserMeResponse(String userType, Long id, String username, String imageUrl) {
        this.userType = userType;
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
    }
}
