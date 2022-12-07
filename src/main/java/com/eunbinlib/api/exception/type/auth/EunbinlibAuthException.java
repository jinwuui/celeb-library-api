package com.eunbinlib.api.exception.type.auth;

public abstract class EunbinlibAuthException extends RuntimeException {

    public EunbinlibAuthException(final String message) {
        super(message);
    }

    public abstract int getStatusCode();
}
