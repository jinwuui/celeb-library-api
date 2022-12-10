package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.utils.AuthService;
import com.eunbinlib.api.auth.utils.AuthorizationExtractor;
import com.eunbinlib.api.dto.response.TokenResponse;
import com.eunbinlib.api.exception.type.auth.UnsupportedMethodException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class JwtRefreshInterceptor implements HandlerInterceptor {

    public static final String TOKEN_REFRESH_URL = "/api/auth/token/refresh";

    private final AuthService authService;

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        validateRequestMethod(request);

        String refreshToken = AuthorizationExtractor.extractToken(request);

        String accessToken = authService.renewAccessToken(refreshToken);

        prepareResponse(response, accessToken, refreshToken);

        return false;
    }

    private void validateRequestMethod(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            throw new UnsupportedMethodException();
        }
    }

    private void prepareResponse(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        response.setStatus(SC_OK);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        TokenResponse body = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        objectMapper.writeValue(response.getWriter(), body);
    }
}
