package com.eunbinlib.api.exception.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class EunbinlibException extends RuntimeException {

    private final Map<String, String> validation = new HashMap<>();

    public EunbinlibException(String message) {
        super(message);
    }

    public EunbinlibException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
