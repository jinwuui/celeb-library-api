package com.eunbinlib.api.exception.type.auth;

import com.eunbinlib.api.exception.type.EunbinlibException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends EunbinlibException {

    private static final String MESSAGE = "접근 권한이 없습니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
