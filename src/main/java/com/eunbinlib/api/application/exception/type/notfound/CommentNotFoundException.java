package com.eunbinlib.api.application.exception.type.notfound;

public class CommentNotFoundException extends NotFoundException {

    private static final String ENTITY = "댓글";

    public CommentNotFoundException() {
        super(ENTITY);
    }
}
