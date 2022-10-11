package com.eunbinlib.api.security.filter;

import com.eunbinlib.api.domain.entity.user.Guest;
import com.eunbinlib.api.domain.request.LoginReq;
import com.eunbinlib.api.domain.response.LoginRes;
import com.eunbinlib.api.repository.user.UserRepository;
import com.eunbinlib.api.security.model.CustomUserDetails;
import com.eunbinlib.api.security.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.persistence.EntityExistsException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class LoginGuestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private static final String LOGIN_GUEST_FILTER_URL = "/api/login/guest";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public LoginGuestAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.objectMapper = new ObjectMapper();
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;

        this.setFilterProcessesUrl(LOGIN_GUEST_FILTER_URL);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        // request에 있는 username과 password를 파싱해서 자바 Object로 받기
        LoginReq loginReq;
        try {
            loginReq = objectMapper.readValue(request.getInputStream(), LoginReq.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (loginReq == null) {
            throw new AuthenticationServiceException("Authentication failed: invalid login info");
        }

        // 게스트 등록
        registerGuest(loginReq);

        // 아이디 & 패스워드 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginReq.getUsername(),
                        loginReq.getPassword());

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        log.info("토큰 생성: username={}", authenticationToken.getName());

        return authenticate;
    }

    private void registerGuest(LoginReq loginReq) {
        boolean isExist = userRepository.existsByUsername(loginReq.getUsername());

        if (isExist) {
            throw new EntityExistsException("이미 존재하는 게스트입니다.");
        }

        Guest guest = Guest.builder()
                .username(loginReq.getUsername())
                .password(passwordEncoder.encode(loginReq.getPassword()))
                .build();

        userRepository.save(guest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        // 토큰 생성
        String accessToken = jwtUtils.createAccessToken(customUserDetails);
        String refreshToken = jwtUtils.createRefreshToken(customUserDetails);

        // ObjectMapper 로 토큰을 json string 으로 변환
        LoginRes loginRes = LoginRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        String body = objectMapper.writeValueAsString(loginRes);

        // body 에 토큰 넣기
        response.setStatus(SC_OK);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.getWriter().write(body);
    }
}
