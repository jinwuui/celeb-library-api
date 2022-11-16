package com.eunbinlib.api.config.auth;

import com.eunbinlib.api.exception.type.Unauthorized;
import com.eunbinlib.api.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            Optional<String> token = jwtUtils.extractToken(request);

            if (token.isEmpty()) throw new Unauthorized();

            Claims jwt = jwtUtils.verifyToken(token.get());

            String username = jwt.getSubject();
            request.setAttribute("username", username);

            return true;
        } catch (Unauthorized e) {
            throw e;
        } catch (Exception e) {
            log.error("unexpected error in AuthInterceptor : ", e);
            throw e;
        }
    }
}
