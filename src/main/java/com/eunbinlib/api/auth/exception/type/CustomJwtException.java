package com.eunbinlib.api.auth.exception.type;

import org.springframework.http.HttpStatus;

public class CustomJwtException extends EunbinlibAuthException {

    private static final String MESSAGE = "인증되지 않은 유저입니다 - 토큰 오류";

    public CustomJwtException() {
        super(MESSAGE);
    }

    public CustomJwtException(final Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
