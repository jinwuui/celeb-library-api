package com.eunbinlib.api.exception.type.application.notfound;

public class CommentNotFoundException extends NotFoundException {

    private static final String ENTITY = "댓글";

    public CommentNotFoundException() {
        super(ENTITY);
    }
}
