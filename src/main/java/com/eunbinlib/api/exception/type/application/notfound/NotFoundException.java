package com.eunbinlib.api.exception.type.application.notfound;

import com.eunbinlib.api.exception.type.application.EunbinlibException;
import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends EunbinlibException {

    private static final String POSTFIX = "을(를) 찾을 수 없습니다.";

    public NotFoundException(final String entity) {
        super(entity + POSTFIX);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
