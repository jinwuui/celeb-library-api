package com.eunbinlib.api.auth.usercontext;

import com.eunbinlib.api.domain.user.User;

public interface UserContextRepository {

    void saveUserInfo(String accessToken, String refreshToken, User user);

    User findUserInfoByAccessToken(String accessToken);

    void updateAccessToken(String newAccessToken, String refreshToken);

    void expireUserInfoContext(String refreshToken);

}
