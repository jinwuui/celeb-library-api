package com.eunbinlib.api.auth.utils;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.LoginRequest;
import com.eunbinlib.api.dto.response.TokenResponse;
import com.eunbinlib.api.exception.type.application.ForbiddenAccessException;
import com.eunbinlib.api.exception.type.auth.InvalidLoginInfoException;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import com.eunbinlib.api.utils.EncryptUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static com.eunbinlib.api.auth.data.AuthProperties.USERNAME;
import static com.eunbinlib.api.auth.data.AuthProperties.USER_TYPE;
import static com.eunbinlib.api.auth.data.RedisCacheKey.USER_SESSION;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthService {

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    public static void authorizePassOnlyMember(UserSession userSession) {
        String userType = userSession.getUserType();
        if (StringUtils.equals(userType, "guest")) {
            throw new ForbiddenAccessException();
        }
    }

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

    @Cacheable(key = "#accessToken", value = USER_SESSION)
    public UserSession validateAccessToken(String accessToken) {
        Claims claims = jwtUtils.validateAccessToken(accessToken);

        String username = claims.get(USERNAME, String.class);

        User findUser = userRepository.findByUsername(username)
                .orElseThrow(UnauthorizedException::new);

        return UserSession.builder()
                .id(findUser.getId())
                .username(findUser.getUsername())
                .userType(findUser.getUserType())
                .build();
    }

    public String renewAccessToken(String refreshToken) {
        Claims claims = jwtUtils.validateRefreshToken(refreshToken);

        String username = claims.get(USERNAME, String.class);
        String userType = claims.get(USER_TYPE, String.class);

        return jwtUtils.createAccessToken(userType, username);
    }
}
