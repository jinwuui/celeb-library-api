package com.eunbinlib.api.config;

import com.eunbinlib.api.auth.JwtAuthInterceptor;
import com.eunbinlib.api.auth.JwtAuthResolver;
import com.eunbinlib.api.auth.JwtRefreshInterceptor;
import com.eunbinlib.api.auth.LoginAuthInterceptor;
import com.eunbinlib.api.auth.utils.AuthService;
import com.eunbinlib.api.exception.handler.AuthHandlerExceptionResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.eunbinlib.api.auth.JwtRefreshInterceptor.TOKEN_REFRESH_URL;
import static com.eunbinlib.api.auth.LoginAuthInterceptor.LOGIN_URL;
import static com.eunbinlib.api.controller.UserController.JOIN_GUEST_URL;
import static com.eunbinlib.api.controller.UserController.JOIN_MEMBER_URL;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private static final String[] AUTH_WHITE_LIST = {LOGIN_URL, TOKEN_REFRESH_URL, JOIN_MEMBER_URL, JOIN_GUEST_URL, "/error"};

    private final AuthService authService;

    private final ObjectMapper objectMapper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor(authService))
                .addPathPatterns("/**")
                .excludePathPatterns(AUTH_WHITE_LIST);

        registry.addInterceptor(new JwtRefreshInterceptor(authService, objectMapper))
                .addPathPatterns(TOKEN_REFRESH_URL);

        registry.addInterceptor(new LoginAuthInterceptor(authService, objectMapper))
                .addPathPatterns(LOGIN_URL);
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new AuthHandlerExceptionResolver());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new JwtAuthResolver());
    }
}
