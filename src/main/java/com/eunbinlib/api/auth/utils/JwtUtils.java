package com.eunbinlib.api.auth.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.eunbinlib.api.auth.data.JwtProperties.*;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtils {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-time}")
    private Long accessTokenExpirationTime;

    @Value("${jwt.refresh-token-expiration-time}")
    private Long refreshTokenExpirationTime;

    public String createAccessToken(String userType, String username) {
        return createToken(userType, username, accessTokenExpirationTime, ACCESS_TOKEN);
    }

    public String createRefreshToken(String userType, String username) {
        return createToken(userType, username, refreshTokenExpirationTime, REFRESH_TOKEN);
    }

    public Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            return Optional.empty();
        }

        return Optional.of(header.replace(TOKEN_PREFIX, ""));
    }

    public Claims verifyAccessToken(String token) {
        return Jwts.parser()
                .require(TOKEN_TYPE, ACCESS_TOKEN)
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims verifyRefreshToken(String token) {
        return Jwts.parser()
                .require(TOKEN_TYPE, REFRESH_TOKEN)
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    private String createToken(String userType, String username, Long tokenExpirationTime, String tokenType) {

        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + tokenExpirationTime);

        return Jwts.builder()
                .setExpiration(expiration)
                .setIssuedAt(now)
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())

                .setSubject(username)
                .claim(USER_TYPE, userType)
                .claim(TOKEN_TYPE, tokenType)
                .compact();
    }
}
