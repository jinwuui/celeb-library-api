package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.utils.AuthService;
import com.eunbinlib.api.dto.request.LoginRequest;
import com.eunbinlib.api.dto.response.TokenResponse;
import com.eunbinlib.api.exception.type.auth.InvalidLoginInfoException;
import com.eunbinlib.api.exception.type.auth.LoginFailedException;
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
public class LoginAuthInterceptor implements HandlerInterceptor {

    public static final String LOGIN_URL = "/api/auth/login";

    private final AuthService authService;

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        validateRequestMethod(request);

        try {
            LoginRequest loginRequest = extractLoginRequest(request);

            TokenResponse tokenResponse = authService.authenticate(loginRequest);

            prepareResponse(response, tokenResponse);

            return false;
        } catch (InvalidLoginInfoException e) {
            log.error("LoginAuthInterceptor: ", e);
            throw e;
        } catch (Exception e) {
            log.error("LoginAuthInterceptor: ", e);
            throw new LoginFailedException(e);
        }
    }

    private void validateRequestMethod(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            throw new UnsupportedMethodException();
        }
    }

    private LoginRequest extractLoginRequest(HttpServletRequest request) throws IOException {
        return objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    }

    private void prepareResponse(HttpServletResponse response, TokenResponse tokenResponse) throws IOException {
        response.setStatus(SC_OK);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}
