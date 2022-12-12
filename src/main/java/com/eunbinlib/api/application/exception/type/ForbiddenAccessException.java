package com.eunbinlib.api.application.exception.type;

import org.springframework.http.HttpStatus;

public class ForbiddenAccessException extends EunbinlibException {

    private static final String MESSAGE = "접근 권한이 없습니다.";

    public ForbiddenAccessException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
