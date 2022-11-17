package com.eunbinlib.api.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenRefreshRes {

    private final String accessToken;

    @Builder
    public TokenRefreshRes(String accessToken) {
        this.accessToken = accessToken;
    }
}
