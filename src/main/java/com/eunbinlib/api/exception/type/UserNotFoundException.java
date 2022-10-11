package com.eunbinlib.api.exception.type;

/**
 * status -> 404
 */
public class UserNotFoundException extends EunbinlibException {

    private static final String MESSAGE = "존재하지 않는 사용자입니다.";

    public UserNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }

}
