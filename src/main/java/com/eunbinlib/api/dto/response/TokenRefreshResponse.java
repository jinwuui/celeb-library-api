package com.eunbinlib.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenRefreshResponse {

    private final String accessToken;

    @Builder
    public TokenRefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
