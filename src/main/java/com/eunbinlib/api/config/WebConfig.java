package com.eunbinlib.api.config;

import com.eunbinlib.api.config.auth.JwtAuthInterceptor;
import com.eunbinlib.api.config.auth.JwtAuthResolver;
import com.eunbinlib.api.config.auth.LoginAuthInterceptor;
import com.eunbinlib.api.repository.user.UserRepository;
import com.eunbinlib.api.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private static final String[] authWhiteList = {"/api/auth/login", "/api/auth/login/guest", "/api/users"};

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor(jwtUtils))
                .excludePathPatterns(authWhiteList);
        registry.addInterceptor(new LoginAuthInterceptor(jwtUtils, userRepository))
                .addPathPatterns("/api/auth/login", "/api/auth/login/guest");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new JwtAuthResolver());
    }
}
