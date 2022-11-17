package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.response.TokenRefreshRes;
import com.eunbinlib.api.exception.type.UnsupportedMethodException;
import com.eunbinlib.api.exception.type.auth.Unauthorized;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.eunbinlib.api.auth.utils.AuthUtils.injectExceptionToRequest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class JwtRefreshInterceptor implements HandlerInterceptor {

    public static final String TOKEN_REFRESH_URL = "/api/auth/token/refresh";

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    public JwtRefreshInterceptor(JwtUtils jwtUtils, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            // HTTP METHOD 검사
            if (!HttpMethod.POST.matches(request.getMethod())) {
                throw new UnsupportedMethodException();
            }

            Optional<String> token = jwtUtils.extractToken(request);

            if (token.isEmpty()) throw new Unauthorized();

            Claims jwt = jwtUtils.verifyRefreshToken(token.get());

            String username = jwt.getSubject();

            String body = createAccessJwtString(username);

            // setting response
            response.setStatus(SC_OK);
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.name());
            response.getWriter().write(body);

            return false;
        } catch (Exception e) {
            log.error("JwtRefreshInterceptor: ", e);
            injectExceptionToRequest(request, e);
            return true;
        }
    }

    private String createAccessJwtString(String username) throws IOException {
        String accessToken = jwtUtils.createAccessToken(username);

        TokenRefreshRes tokenRefreshRes = TokenRefreshRes.builder()
                .accessToken(accessToken)
                .build();

        return objectMapper.writeValueAsString(tokenRefreshRes);
    }
}
