package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.auth.utils.AuthService;
import com.eunbinlib.api.auth.utils.AuthorizationExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eunbinlib.api.auth.data.RedisCacheKey.USER_SESSION;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = AuthorizationExtractor.extractToken(request);

        UserSession userSession = authService.validateAccessToken(accessToken);

        request.setAttribute(USER_SESSION, userSession);

        return true;
    }
}
