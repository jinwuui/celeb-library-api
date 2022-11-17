package com.eunbinlib.api.config;

import com.eunbinlib.api.auth.*;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor(jwtUtils))
                .addPathPatterns("/**")
                .excludePathPatterns(AUTH_WHITE_LIST)
                .order(1);

        registry.addInterceptor(new JwtRefreshInterceptor(jwtUtils, objectMapper))
                .addPathPatterns(TOKEN_REFRESH_URL)
                .order(2);

        registry.addInterceptor(new LoginAuthInterceptor(jwtUtils, userRepository))
                .addPathPatterns(LOGIN_URL)
                .order(3);

        registry.addInterceptor(new InterceptorExceptionInterceptor(objectMapper))
                .addPathPatterns("/**")
                .order(4);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new JwtAuthResolver());
    }
}
