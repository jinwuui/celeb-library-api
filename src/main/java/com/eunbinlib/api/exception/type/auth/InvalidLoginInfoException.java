package com.eunbinlib.api.exception.type.auth;

import com.eunbinlib.api.exception.type.EunbinlibException;
import org.springframework.http.HttpStatus;

public class InvalidLoginInfoException extends EunbinlibException {

    private static final String MESSAGE = "잘못된 로그인 정보입니다.";

    public InvalidLoginInfoException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
