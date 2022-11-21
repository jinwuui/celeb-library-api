package com.eunbinlib.api.exception.type.notfound;

public class PostNotFoundException extends NotFoundException {

    private static final String ENTITY = "글";

    public PostNotFoundException() {
        super(ENTITY);
    }
}
