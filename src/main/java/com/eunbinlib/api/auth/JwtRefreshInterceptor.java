package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.usercontext.UserContextRepository;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.response.TokenRefreshRes;
import com.eunbinlib.api.exception.type.UnsupportedMethodException;
import com.eunbinlib.api.exception.type.auth.UnauthenticatedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eunbinlib.api.auth.data.JwtProperties.USER_TYPE;
import static com.eunbinlib.api.auth.utils.AuthUtils.injectExceptionToRequest;
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

        String refreshToken = null;
        try {
            // HTTP METHOD 검사
            if (!HttpMethod.POST.matches(request.getMethod())) {
                throw new UnsupportedMethodException();
            }

            refreshToken = jwtUtils.extractToken(request)
                    .orElseThrow(UnauthenticatedException::new);

            Claims jwt = jwtUtils.verifyRefreshToken(refreshToken);

            String userType = jwt.get(USER_TYPE, String.class);
            String username = jwt.getSubject();

            TokenRefreshRes tokenRefreshRes = createTokenRefreshRes(userType, username);

            // setting response
            response.setStatus(SC_OK);
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.name());
            objectMapper.writeValue(response.getWriter(), tokenRefreshRes);

            userContextRepository.updateAccessToken(
                    tokenRefreshRes.getAccessToken(),
                    refreshToken
            );
            return false;
        } catch (Exception e) {
            log.error("JwtRefreshInterceptor: ", e);
            if (refreshToken != null) {
                userContextRepository.expireUserInfoContext(refreshToken);
            }
            injectExceptionToRequest(request, e);

            return true;
        }
    }

    private TokenRefreshRes createTokenRefreshRes(String userType, String username) {
        String accessToken = jwtUtils.createAccessToken(userType, username);

        return  TokenRefreshRes.builder()
                .accessToken(accessToken)
                .build();
    }
}
