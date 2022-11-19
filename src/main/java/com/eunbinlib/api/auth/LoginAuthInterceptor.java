package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.usercontext.UserContextRepository;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.entity.user.User;
import com.eunbinlib.api.domain.request.LoginReq;
import com.eunbinlib.api.domain.response.LoginRes;
import com.eunbinlib.api.exception.type.UnsupportedMethodException;
import com.eunbinlib.api.exception.type.auth.InvalidLoginInfoException;
import com.eunbinlib.api.repository.user.UserRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

            LoginReq loginReq = objectMapper.readValue(request.getInputStream(), LoginReq.class);


            User user = authenticate(
                    loginReq.getUsername(),
                    loginReq.getPassword()
            );


            LoginRes loginRes = createLoginRes(user);

            // setting response
            response.setStatus(SC_OK);
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.name());

            objectMapper.writeValue(response.getWriter(), loginRes);

            // save logged-in user // TODO: change to Redis
            userContextRepository.saveUserInfo(loginRes.getAccessToken(), loginRes.getRefreshToken(), user);

            return false;
        } catch (Exception e) {
            log.error("LoginAuthInterceptor: ", e);
            injectExceptionToRequest(request, e);
            return true;
        }
    }

    private LoginRes createLoginRes(User user) {
        String accessToken = jwtUtils.createAccessToken(user.getUserType(), user.getUsername());
        String refreshToken = jwtUtils.createRefreshToken(user.getUserType(), user.getUsername());

        return LoginRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private User authenticate(String username, String password) {
        User findUser = userRepository.findByUsername(username)
                .orElseThrow(InvalidLoginInfoException::new);

        boolean isInvalidPassword = !StringUtils.equals(password, findUser.getPassword());
        if (isInvalidPassword) {
            throw new InvalidLoginInfoException();
        }

        return findUser;
    }
}
