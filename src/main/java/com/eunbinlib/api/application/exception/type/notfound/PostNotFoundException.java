package com.eunbinlib.api.application.exception.type.notfound;

public class PostNotFoundException extends NotFoundException {

    private static final String ENTITY = "글";

    public PostNotFoundException() {
        super(ENTITY);
    }
}
