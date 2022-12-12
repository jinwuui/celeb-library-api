package com.eunbinlib.api.auth.exception.type;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends EunbinlibAuthException {

    private static final String MESSAGE = "인증되지 않은 유저입니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }

    public UnauthorizedException(final Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
