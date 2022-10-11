package com.eunbinlib.api.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginRes {

    private final String accessToken;
    private final String refreshToken;

    @Builder
    public LoginRes(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
