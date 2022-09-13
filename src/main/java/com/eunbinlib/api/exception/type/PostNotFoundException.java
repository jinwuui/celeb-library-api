package com.eunbinlib.api.exception.type;

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
        return 404;
    }

}
