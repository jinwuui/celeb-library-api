package com.eunbinlib.api.application.exception.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class EunbinlibException extends RuntimeException {

    private final Map<String, String> validation = new HashMap<>();

    public EunbinlibException(final String message) {
        super(message);
    }

    public abstract int getStatusCode();

    public void addValidation(final String fieldName, final String message) {
        validation.put(fieldName, message);
    }
}
