package com.eunbinlib.api.auth.data;

public interface JwtProperties {

    String TOKEN_TYPE = "tokenType";
    String USER_TYPE = "userType";

    String USERNAME = "username";

    String USER_INFO = "userInfo";

    String ACCESS_TOKEN = "accessToken";
    String REFRESH_TOKEN = "refreshToken";

    String TOKEN_PREFIX = "Bearer ";

    String HEADER_STRING = "Authorization";

}
