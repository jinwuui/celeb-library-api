package com.eunbinlib.api.exception.type;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

public class EunbinlibIllegalArgumentException extends EunbinlibException {

    private static final String MESSAGE = "부적합한 인자가 전달 되었습니다.";

    public EunbinlibIllegalArgumentException() {
        super(MESSAGE);
    }

    public EunbinlibIllegalArgumentException(final String message) {
        super(message);
    }

    public EunbinlibIllegalArgumentException(final String fieldName, final String message) {
        super(StringUtils.join(MESSAGE, " {\"" + fieldName + "\" : \"" + message + "\"}"));
        addValidation(fieldName, message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
