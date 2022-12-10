package com.eunbinlib.api.exception.type.application.notfound;

public class BlockNotFoundException extends NotFoundException {

    private static final String ENTITY = "사용자 간 차단";

    public BlockNotFoundException() {
        super(ENTITY);
    }
}
