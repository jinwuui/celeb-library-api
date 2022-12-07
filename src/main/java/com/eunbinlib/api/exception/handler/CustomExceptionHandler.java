package com.eunbinlib.api.exception.handler;

import com.eunbinlib.api.dto.response.ErrorResponse;
import com.eunbinlib.api.exception.type.application.EunbinlibException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class CustomExceptionHandler {

    @ResponseBody
    @ExceptionHandler(EunbinlibException.class)
    public ResponseEntity<ErrorResponse> eunbinlibException(EunbinlibException e) {
        int statusCode = e.getStatusCode();

        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();

        return ResponseEntity
                .status(statusCode)
                .body(body);
    }

}
