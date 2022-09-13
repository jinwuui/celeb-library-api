package com.eunbinlib.api.exception.type;

/**
 * @NotBlank 등의 어노테이션 필드 검증 방식으로 처리할 수 없는
 * status -> 400
 */
public class InvalidRequestException extends EunbinlibException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequestException() {
        super(MESSAGE);
    }

    public InvalidRequestException(String fieldName, String message) {
        super(MESSAGE);
        addValidation(fieldName, message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }

}
