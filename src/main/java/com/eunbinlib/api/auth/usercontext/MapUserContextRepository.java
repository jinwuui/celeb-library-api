package com.eunbinlib.api.auth.usercontext;

import com.eunbinlib.api.domain.entity.user.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MapUserContextRepository implements UserContextRepository {

    private static final Map<String, User> storage = new ConcurrentHashMap<>();

    private static final Map<String, String> refreshTokenToAccessTokenStorage = new ConcurrentHashMap<>();

    public void saveUserInfo(String accessToken, String refreshToken, User user) {
        refreshTokenToAccessTokenStorage.put(refreshToken, accessToken);
        storage.put(accessToken, user);
    }

    public User findUserInfoByAccessToken(String accessToken) {
        return storage.get(accessToken);
    }

    public void updateAccessToken(String newAccessToken, String refreshToken) {
        String oldAccessToken = refreshTokenToAccessTokenStorage.get(refreshToken);
        refreshTokenToAccessTokenStorage.put(refreshToken, newAccessToken);

        storage.put(newAccessToken, storage.get(oldAccessToken));
        storage.remove(oldAccessToken);
    }

    public void expireUserInfoContext(String refreshToken) {
        if (refreshTokenToAccessTokenStorage.containsKey(refreshToken)) {
            String accessToken = refreshTokenToAccessTokenStorage.get(refreshToken);
            refreshTokenToAccessTokenStorage.remove(refreshToken);

            storage.remove(accessToken);
        }
    }
}
