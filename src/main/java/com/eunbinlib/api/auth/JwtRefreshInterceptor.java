package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.usercontext.UserContextRepository;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.dto.response.TokenResponse;
import com.eunbinlib.api.exception.type.auth.UnsupportedMethodException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.eunbinlib.api.auth.data.AuthProperties.USER_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class JwtRefreshInterceptor implements HandlerInterceptor {

    public static final String TOKEN_REFRESH_URL = "/api/auth/token/refresh";

    private final JwtUtils jwtUtils;

    private final ObjectMapper objectMapper;

    private final UserContextRepository userContextRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        validateRequestMethod(request);

        String refreshToken = null;

        try {
            refreshToken = jwtUtils.extractToken(request);

            Claims claims = jwtUtils.verifyRefreshToken(refreshToken);

            String userType = claims.get(USER_TYPE, String.class);
            String username = claims.getSubject();

            TokenResponse tokenResponse = createTokenResponse(refreshToken, userType, username);

            userContextRepository.updateAccessToken(
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken()
            );

            prepareResponse(response, tokenResponse);

            return false;
        } catch (Exception e) {
            log.error("JwtRefreshInterceptor: ", e);
            if (refreshToken != null) {
                userContextRepository.expireUserInfoContext(refreshToken);
            }
            throw e;
        }
    }

    private void validateRequestMethod(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            throw new UnsupportedMethodException();
        }
    }

    private TokenResponse createTokenResponse(String refreshToken, String userType, String username) {
        String accessToken = jwtUtils.createAccessToken(userType, username);

        return  TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void prepareResponse(HttpServletResponse response, TokenResponse tokenResponse) throws IOException {
        response.setStatus(SC_OK);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}
