package com.eunbinlib.api.exception.type;

import org.springframework.http.HttpStatus;

/**
 * status -> 404
 */
public class PostNotFoundException extends EunbinlibException {

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public PostNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }

}
