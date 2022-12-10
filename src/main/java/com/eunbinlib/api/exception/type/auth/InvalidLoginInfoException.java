package com.eunbinlib.api.exception.type.auth;

import org.springframework.http.HttpStatus;

public class InvalidLoginInfoException extends EunbinlibAuthException {

    private static final String MESSAGE = "아이디/비밀번호가 올바르지 않습니다.";

    public InvalidLoginInfoException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
