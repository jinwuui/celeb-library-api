package com.eunbinlib.api.auth.exception.type;

public abstract class EunbinlibAuthException extends RuntimeException {

    public EunbinlibAuthException(final String message) {
        super(message);
    }

    public EunbinlibAuthException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();
}
