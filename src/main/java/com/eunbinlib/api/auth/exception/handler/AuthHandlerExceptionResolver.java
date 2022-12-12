package com.eunbinlib.api.auth.exception.handler;

import com.eunbinlib.api.auth.exception.type.EunbinlibAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AuthHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex instanceof EunbinlibAuthException) {
                EunbinlibAuthException authEx = (EunbinlibAuthException) ex;
                response.sendError(authEx.getStatusCode(), authEx.getMessage());

                return new ModelAndView();
            }
        } catch (Exception e) {
            log.error("auth resolver exception:", e);
        }

        return null;
    }
}
