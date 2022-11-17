package com.eunbinlib.api.auth.utils;

import javax.servlet.http.HttpServletRequest;

public class AuthUtils {

    public static final String EXCEPTION = "exception";

    public static <T extends Throwable> void injectExceptionToRequest(HttpServletRequest request, T exception) {
        request.setAttribute(EXCEPTION, exception);
    }
}
