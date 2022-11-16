package com.eunbinlib.api.exception.type;

import org.springframework.http.HttpStatus;

public class InvalidLoginInfoException extends EunbinlibException {

    private static final String MESSAGE = "잘못된 로그인 정보입니다.";

    public InvalidLoginInfoException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
