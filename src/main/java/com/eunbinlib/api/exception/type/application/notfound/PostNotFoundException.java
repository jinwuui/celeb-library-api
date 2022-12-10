package com.eunbinlib.api.exception.type.application.notfound;

public class PostNotFoundException extends NotFoundException {

    private static final String ENTITY = "글";

    public PostNotFoundException() {
        super(ENTITY);
    }
}
