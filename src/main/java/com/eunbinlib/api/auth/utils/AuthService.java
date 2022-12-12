package com.eunbinlib.api.auth.utils;

import com.eunbinlib.api.auth.data.MemberSession;
import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.application.domain.repository.user.UserRepository;
import com.eunbinlib.api.application.domain.user.User;
import com.eunbinlib.api.application.dto.request.LoginRequest;
import com.eunbinlib.api.application.dto.response.TokenResponse;
import com.eunbinlib.api.application.exception.type.ForbiddenAccessException;
import com.eunbinlib.api.auth.exception.type.InvalidLoginInfoException;
import com.eunbinlib.api.auth.exception.type.UnauthorizedException;
import com.eunbinlib.api.application.utils.EncryptUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.eunbinlib.api.auth.data.AuthProperties.USERNAME;
import static com.eunbinlib.api.auth.data.AuthProperties.USER_TYPE;
import static com.eunbinlib.api.auth.data.RedisCacheKey.USER_SESSION;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    public TokenResponse authenticate(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User findUser = userRepository.findByUsername(username)
                .orElseThrow(InvalidLoginInfoException::new);

        if (EncryptUtils.isNotMatch(password, findUser.getPassword())) {
            throw new InvalidLoginInfoException();
        }

        return createTokenResponse(findUser);
    }

    private TokenResponse createTokenResponse(User user) {
        String accessToken = jwtUtils.createAccessToken(user.getUserType(), user.getUsername());
        String refreshToken = jwtUtils.createRefreshToken(user.getUserType(), user.getUsername());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void validateAccessToken(String accessToken) {
        jwtUtils.validateAccessToken(accessToken);
    }

    public String renewAccessToken(String refreshToken) {
        Claims claims = jwtUtils.validateRefreshToken(refreshToken);

        String username = claims.get(USERNAME, String.class);
        String userType = claims.get(USER_TYPE, String.class);

        return jwtUtils.createAccessToken(userType, username);
    }

    @Cacheable(key = "#accessToken", value = USER_SESSION)
    public UserSession getSession(String accessToken) {
        User findUser = findUserByAccessToken(accessToken);

        return UserSession.from(findUser);
    }

    @Cacheable(key = "#accessToken", value = USER_SESSION)
    public UserSession getMemberSession(String accessToken) {
        User findUser = findUserByAccessToken(accessToken);

        UserSession userSession = UserSession.from(findUser);
        if (userSession instanceof MemberSession) {
            return userSession;
        }

        throw new ForbiddenAccessException();
    }

    private User findUserByAccessToken(String accessToken) {
        String username = jwtUtils.extractUsername(accessToken);
        return userRepository.findByUsername(username)
                .orElseThrow(UnauthorizedException::new);
    }
}
