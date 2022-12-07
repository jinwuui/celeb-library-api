package com.eunbinlib.api.exception.type.auth;

import org.springframework.http.HttpStatus;

public class ForbiddenAuthException extends EunbinlibAuthException {

    private static final String MESSAGE = "접근 권한이 없습니다.";

    public ForbiddenAuthException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
