package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.usercontext.UserContextRepository;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.exception.type.auth.UnauthenticatedException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eunbinlib.api.auth.data.JwtProperties.*;
import static com.eunbinlib.api.auth.utils.AuthUtils.injectExceptionToRequest;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    private final UserContextRepository userContextRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            String accessToken = jwtUtils.extractToken(request)
                    .orElseThrow(UnauthenticatedException::new);

            Claims jwt = jwtUtils.verifyAccessToken(accessToken);

            request.setAttribute(
                    USER_INFO,
                    userContextRepository.findUserInfoByAccessToken(accessToken)
            );
        } catch (Exception e) {
            log.error("JwtAuthInterceptor: ", e);
            injectExceptionToRequest(request, e);
        }

        return true;
    }
}
