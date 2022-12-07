package com.eunbinlib.api.config;

import com.eunbinlib.api.auth.*;
import com.eunbinlib.api.auth.usercontext.MapUserContextRepository;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.repository.user.UserRepository;
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

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private final MapUserContextRepository userContextRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor(jwtUtils, userContextRepository))
                .addPathPatterns("/**")
                .excludePathPatterns(AUTH_WHITE_LIST);

        registry.addInterceptor(new JwtRefreshInterceptor(jwtUtils, objectMapper, userContextRepository))
                .addPathPatterns(TOKEN_REFRESH_URL);

        registry.addInterceptor(new LoginAuthInterceptor(jwtUtils, userRepository, userContextRepository))
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
