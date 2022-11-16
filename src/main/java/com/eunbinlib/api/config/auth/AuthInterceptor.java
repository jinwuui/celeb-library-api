package com.eunbinlib.api.config.auth;

import com.eunbinlib.api.exception.type.Unauthorized;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (true) { // TODO: JWT 인증 체크
            return true;
        }

        throw new Unauthorized();
    }

}
