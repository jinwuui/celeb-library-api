package com.eunbinlib.api.security.filter;

import com.eunbinlib.api.domain.entity.user.User;
import com.eunbinlib.api.exception.type.UserNotFoundException;
import com.eunbinlib.api.repository.user.UserRepository;
import com.eunbinlib.api.security.model.CustomUserDetails;
import com.eunbinlib.api.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtUtils jwtUtils) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 토큰 추출
        String token = jwtUtils.extractToken(request);

        if (!StringUtils.hasText(token)) {
            chain.doFilter(request, response);
            return;
        }

        // 토큰 검증
        Jws<Claims> jwt = jwtUtils.decodeToken(token);

        if (jwt == null) {
            chain.doFilter(request, response);
            return;
        }


        String username = jwt.getBody().getSubject();

        if (StringUtils.hasText(username)) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(UserNotFoundException::new);

            // 인증은 토큰 검증시 끝. 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
            // 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장!
            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            customUserDetails,
                            null,
                            customUserDetails.getAuthorities()
                    );

            // 강제로 시큐리티의 세션에 접근하여 값 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
