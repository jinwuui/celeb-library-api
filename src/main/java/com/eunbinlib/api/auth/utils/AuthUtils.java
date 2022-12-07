package com.eunbinlib.api.auth.utils;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.exception.type.application.ForbiddenAccessException;
import org.apache.commons.lang3.StringUtils;

public class AuthUtils {

    public static final String EXCEPTION = "exception";

    public static void authorizePassOnlyMember(UserSession userSession) {
        String userType = userSession.getUserType();
        if (StringUtils.equals(userType, "guest")) {
            throw new ForbiddenAccessException();
        }
    }
}
