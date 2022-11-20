package com.eunbinlib.api.exception.type.auth;

import com.eunbinlib.api.exception.type.EunbinlibException;
import org.springframework.http.HttpStatus;

public class CustomJwtException extends EunbinlibException {

    private static final String MESSAGE = "인증되지 않은 유저입니다 - 토큰 오류";

    public CustomJwtException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
