package com.eunbinlib.api.security.handler;

import com.eunbinlib.api.domain.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // 인증 핸들러
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(SC_UNAUTHORIZED + "")
                .message(authException.getMessage())
                .build();

        String body = objectMapper.writeValueAsString(errorResponse);

        response.setStatus(SC_UNAUTHORIZED);
        response.getWriter().write(body);

    }
}
