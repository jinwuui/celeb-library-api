package com.eunbinlib.api.auth.utils;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class AuthUtils {

    public static final String EXCEPTION = "exception";

    public static <T extends Throwable> void injectExceptionToRequest(HttpServletRequest request, T exception) {
        request.setAttribute(EXCEPTION, exception);
    }

    public static void authorizeUserSession(UserSession userSession) {
        String userType = userSession.getUserType();
        if (StringUtils.equals(userType, "guest")) {
            throw new UnauthorizedException();
        }
    }
}
