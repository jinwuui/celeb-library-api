package com.eunbinlib.api.exception.type.auth;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends EunbinlibAuthException {

    private static final String MESSAGE = "인증되지 않은 유저입니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
