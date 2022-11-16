package com.eunbinlib.api.security.config;

import com.eunbinlib.api.repository.user.UserRepository;
import com.eunbinlib.api.security.filter.JwtAuthenticationFilter;
import com.eunbinlib.api.security.filter.LoginAuthenticationFilter;
import com.eunbinlib.api.security.filter.LoginGuestAuthenticationFilter;
import com.eunbinlib.api.security.handler.CustomAccessDeniedHandler;
import com.eunbinlib.api.security.handler.CustomAuthenticationEntryPoint;
import com.eunbinlib.api.security.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.eunbinlib.api.security.filter.LoginAuthenticationFilter.*;
import static com.eunbinlib.api.security.filter.LoginGuestAuthenticationFilter.*;

@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] whiteList = {LOGIN_FILTER_URL, LOGIN_GUEST_FILTER_URL, "/api/users"};

    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint(new ObjectMapper());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .httpBasic().disable()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests()
                .antMatchers(whiteList).permitAll()
                .anyRequest().authenticated()
                .and()

                .addFilterBefore(new LoginAuthenticationFilter(authenticationManager(), jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new LoginGuestAuthenticationFilter(authenticationManager(), userRepository, jwtUtils, passwordEncoder()), LoginAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthenticationFilter(authenticationManager(), userRepository, jwtUtils), LoginAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler());
    }
}
