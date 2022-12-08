package com.eunbinlib.api.auth.utils;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.auth.usercontext.UserContextRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.LoginRequest;
import com.eunbinlib.api.dto.response.TokenResponse;
import com.eunbinlib.api.exception.type.application.ForbiddenAccessException;
import com.eunbinlib.api.exception.type.auth.InvalidLoginInfoException;
import com.eunbinlib.api.utils.EncryptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthUtils {


    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    private final UserContextRepository userContextRepository;

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

        TokenResponse tokenResponse = createLoginResponse(findUser);

        // save logged-in findUser // TODO: change to Redis
        userContextRepository.saveUserInfo(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(), findUser);

        return tokenResponse;
    }

    private TokenResponse createLoginResponse(User user) {
        String accessToken = jwtUtils.createAccessToken(user.getUserType(), user.getUsername());
        String refreshToken = jwtUtils.createRefreshToken(user.getUserType(), user.getUsername());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
