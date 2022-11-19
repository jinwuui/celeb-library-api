package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.exception.type.auth.UnauthenticatedException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.eunbinlib.api.auth.data.JwtProperties.USERNAME;
import static com.eunbinlib.api.auth.data.JwtProperties.USER_TYPE;
import static com.eunbinlib.api.auth.utils.AuthUtils.injectExceptionToRequest;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            Optional<String> token = jwtUtils.extractToken(request);

            if (token.isEmpty()) {
                throw new UnauthenticatedException();
            }

            Claims jwt = jwtUtils.verifyAccessToken(token.get());

            String userType = jwt.get(USER_TYPE, String.class);
            String username = jwt.getSubject();

            request.setAttribute(USER_TYPE, userType);
            request.setAttribute(USERNAME, username);
        } catch (Exception e) {
            log.error("JwtAuthInterceptor: ", e);
            injectExceptionToRequest(request, e);
        }

        return true;
    }
}
