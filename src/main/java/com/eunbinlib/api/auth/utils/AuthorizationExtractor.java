package com.eunbinlib.api.auth.utils;

import com.eunbinlib.api.auth.exception.type.UnauthorizedException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class AuthorizationExtractor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String TOKEN_PREFIX = "Bearer ";

    public static String extractToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION_HEADER);

        while (headers.hasMoreElements()) {
            String value = headers.nextElement();

            if (StringUtils.startsWithIgnoreCase(value, TOKEN_PREFIX)) {
                return StringUtils.replaceIgnoreCase(value, TOKEN_PREFIX, "");
            }
        }

        throw new UnauthorizedException();
    }
}
