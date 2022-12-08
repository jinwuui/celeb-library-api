package com.eunbinlib.api.auth.utils;


import com.eunbinlib.api.exception.type.auth.CustomJwtException;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

import static com.eunbinlib.api.auth.data.AuthProperties.*;


@Slf4j
@Component
public class JwtUtils {

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

                    .setSubject(username)
                    .claim(USER_TYPE, userType)
                    .claim(TOKEN_TYPE, tokenType)
                    .compact();
        } catch (Exception e) {
            throw new CustomJwtException(e);
        }
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);

        if (StringUtils.isEmpty(header) || !header.startsWith(TOKEN_PREFIX)) {
            throw new UnauthorizedException();
        }

        return header.replace(TOKEN_PREFIX, "");
    }

    public Claims verifyAccessToken(String token) {
        try {
            return jwtParser.require(TOKEN_TYPE, ACCESS_TOKEN)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new CustomJwtException(e);
        }
    }

    public Claims verifyRefreshToken(String token) {
        try {
            return jwtParser.require(TOKEN_TYPE, REFRESH_TOKEN)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new CustomJwtException(e);
        }
    }
}
