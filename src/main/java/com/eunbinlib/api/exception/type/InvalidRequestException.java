package com.eunbinlib.api.exception.type;

import org.springframework.http.HttpStatus;

/**
 * @NotBlank 등의 어노테이션 필드 검증 방식으로 처리할 수 없는
 * status -> 400
 */
public class InvalidRequestException extends EunbinlibException {

    private static final String MESSAGE = "";

    public InvalidRequestException() {
        super(MESSAGE);
    }

    public InvalidRequestException(final String fieldName, final String message) {
        super(MESSAGE);
        addValidation(fieldName, message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

}
