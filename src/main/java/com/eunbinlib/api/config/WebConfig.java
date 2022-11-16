package com.eunbinlib.api.config;

import com.eunbinlib.api.auth.JwtAuthInterceptor;
import com.eunbinlib.api.auth.JwtAuthResolver;
import com.eunbinlib.api.auth.LoginAuthInterceptor;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.eunbinlib.api.auth.LoginAuthInterceptor.LOGIN_URL;
import static com.eunbinlib.api.controller.UserController.JOIN_GUEST_URL;
import static com.eunbinlib.api.controller.UserController.JOIN_MEMBER_URL;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private static final String[] AUTH_WHITE_LIST = {LOGIN_URL, JOIN_MEMBER_URL, JOIN_GUEST_URL};

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor(jwtUtils))
                .excludePathPatterns(AUTH_WHITE_LIST);
        registry.addInterceptor(new LoginAuthInterceptor(jwtUtils, userRepository))
                .addPathPatterns(LOGIN_URL);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new JwtAuthResolver());
    }
}
