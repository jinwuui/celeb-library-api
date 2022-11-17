package com.eunbinlib.api.exception.type;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends EunbinlibException {

    private static final String MESSAGE = "유저를 찾을 수 없습니다.";

    public UserNotFoundException() {
        super(MESSAGE);
    }

    public UserNotFoundException(String username) {
        super(MESSAGE + " - " + username);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
