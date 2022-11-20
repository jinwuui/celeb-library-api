package com.eunbinlib.api.auth;

import com.eunbinlib.api.dto.response.ErrorResponse;
import com.eunbinlib.api.exception.type.EunbinlibException;
import com.eunbinlib.api.exception.type.auth.CustomJwtException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.eunbinlib.api.auth.utils.AuthUtils.EXCEPTION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class InterceptorExceptionInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Object attribute = request.getAttribute(EXCEPTION);

        if (attribute == null) {
            return true;
        } else if (attribute instanceof EunbinlibException) {
            EunbinlibException e = (EunbinlibException) attribute;
            setExceptionResponse(response, e);
            return false;
        } else if (attribute instanceof JwtException) {
            JwtException e = (JwtException) attribute;
            setExceptionResponse(response, new CustomJwtException());
            return false;
        }
        else if (attribute instanceof Exception) {
            Exception e = (Exception) attribute;
            setExceptionResponse(response, e);
            return false;
        }

        return true;
    }

    private void setExceptionResponse(HttpServletResponse response, EunbinlibException e) throws IOException {

        int statusCode = e.getStatusCode();

        response.setStatus(statusCode);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private void setExceptionResponse(HttpServletResponse response, JwtException e) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(e.getMessage())
                .build();

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private void setExceptionResponse(HttpServletResponse response, Exception e) throws IOException {

        response.setStatus(BAD_REQUEST.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(BAD_REQUEST.value()))
                .message("잘못된 요청입니다.")
                .build();

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
