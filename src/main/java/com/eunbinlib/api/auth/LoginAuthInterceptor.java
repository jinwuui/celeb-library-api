package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.usercontext.UserContextRepository;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.LoginRequest;
import com.eunbinlib.api.dto.response.LoginResponse;
import com.eunbinlib.api.exception.type.UnsupportedMethodException;
import com.eunbinlib.api.exception.type.auth.InvalidLoginInfoException;
import com.eunbinlib.api.utils.EncryptUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eunbinlib.api.auth.utils.AuthUtils.injectExceptionToRequest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class LoginAuthInterceptor implements HandlerInterceptor {

    public static final String LOGIN_URL = "/api/auth/login";

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final UserContextRepository userContextRepository;
    private final ObjectMapper objectMapper;

    public LoginAuthInterceptor(JwtUtils jwtUtils, UserRepository userRepository, UserContextRepository userContextRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.userContextRepository = userContextRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            if (!HttpMethod.POST.matches(request.getMethod())) {
                throw new UnsupportedMethodException();
            }

            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            User findUser = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(InvalidLoginInfoException::new);

            authenticate(findUser, loginRequest.getPassword());

            LoginResponse loginResponse = createLoginRes(findUser);

            // setting response
            response.setStatus(SC_OK);
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.name());

            objectMapper.writeValue(response.getWriter(), loginResponse);

            // save logged-in findUser // TODO: change to Redis
            userContextRepository.saveUserInfo(loginResponse.getAccessToken(), loginResponse.getRefreshToken(), findUser);

            return false;
        } catch (Exception e) {
            log.error("LoginAuthInterceptor: ", e);
            injectExceptionToRequest(request, e);
            return true;
        }
    }

    private LoginResponse createLoginRes(User user) {
        String accessToken = jwtUtils.createAccessToken(user.getUserType(), user.getUsername());
        String refreshToken = jwtUtils.createRefreshToken(user.getUserType(), user.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void authenticate(User findUser, String plainPassword) {
        if (EncryptUtils.isNotMatch(plainPassword, findUser.getPassword())) {
            throw new InvalidLoginInfoException();
        }
    }
}
