package com.eunbinlib.api.exception.type.auth;

import org.springframework.http.HttpStatus;

public class UnsupportedMethodException extends EunbinlibAuthException {

    private static final String MESSAGE = "접근 불가능한 HTTP METHOD 입니다.";

    public UnsupportedMethodException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.METHOD_NOT_ALLOWED.value();
    }
}
