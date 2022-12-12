package com.eunbinlib.api.application.exception.type.notfound;

import org.apache.commons.lang3.StringUtils;

public class UserNotFoundException extends NotFoundException {

    private static final String ENTITY = "유저";

    public UserNotFoundException() {
        super(ENTITY);
    }

    public UserNotFoundException(final String username) {
        super(StringUtils.join(username, " ", ENTITY));
    }
}
