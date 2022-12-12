package com.eunbinlib.api.auth.utils;


import com.eunbinlib.api.auth.exception.type.CustomJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

import static com.eunbinlib.api.auth.data.AuthProperties.*;


@Slf4j
@Component
public class JwtUtils {

    private static final String ACCESS_TOKEN = "accessToken";

    private static final String REFRESH_TOKEN = "refreshToken";

    private final String secretKey;

    private final Long accessTokenExpirationTime;

    private final Long refreshTokenExpirationTime;

    private final JwtParser jwtParser;

    public JwtUtils(@Value("${jwt.secret-key}") String secretKey,
                    @Value("${jwt.token.access-expiration-time}") Long accessTokenExpirationTime,
                    @Value("${jwt.token.refresh-expiration-time}") Long refreshTokenExpirationTime
    ) {
        this.secretKey = secretKey;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.jwtParser = Jwts.parser().setSigningKey(this.secretKey);
    }

    public String createAccessToken(String userType, String username) {
        return createToken(userType, username, accessTokenExpirationTime, ACCESS_TOKEN);
    }

    public String createRefreshToken(String userType, String username) {
        return createToken(userType, username, refreshTokenExpirationTime, REFRESH_TOKEN);
    }

    private String createToken(String userType, String username, Long tokenExpirationTime, String tokenType) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + tokenExpirationTime);

        try {
            return Jwts.builder()
                    .setExpiration(expiration)
                    .setIssuedAt(now)
                    .setId(UUID.randomUUID().toString())
                    .signWith(SignatureAlgorithm.HS256, secretKey)

                    .claim(USERNAME, username)
                    .claim(USER_TYPE, userType)
                    .claim(TOKEN_TYPE, tokenType)

                    .compact();
        } catch (Exception e) {
            throw new CustomJwtException(e);
        }
    }

    public Claims validateAccessToken(String accessToken) {
        try {
            return jwtParser.require(TOKEN_TYPE, ACCESS_TOKEN)
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (Exception e) {
            throw new CustomJwtException(e);
        }
    }

    public Claims validateRefreshToken(String refreshToken) {
        try {
            return jwtParser.require(TOKEN_TYPE, REFRESH_TOKEN)
                    .parseClaimsJws(refreshToken)
                    .getBody();
        } catch (Exception e) {
            throw new CustomJwtException(e);
        }
    }

    public String extractUsername(String token) {
        try {
            return jwtParser
                    .parseClaimsJws(token)
                    .getBody()
                    .get(USERNAME, String.class);
        } catch (Exception e) {
            throw new CustomJwtException(e);
        }
    }
}
