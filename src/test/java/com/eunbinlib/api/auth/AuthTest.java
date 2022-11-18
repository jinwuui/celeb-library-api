package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.data.JwtProperties;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.entity.user.Member;
import com.eunbinlib.api.domain.request.LoginReq;
import com.eunbinlib.api.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static com.eunbinlib.api.auth.JwtRefreshInterceptor.TOKEN_REFRESH_URL;
import static com.eunbinlib.api.auth.LoginAuthInterceptor.LOGIN_URL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AuthTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    String accessToken;
    String refreshToken;

    String username = "testUsername";
    String password = "testPassword";
    String nickname = "testNickname";

    @BeforeAll()
    void beforeAll() {
        Member member = Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();

        userRepository.save(member);
        accessToken = JwtProperties.TOKEN_PREFIX + jwtUtils.createAccessToken(username);
        refreshToken = JwtProperties.TOKEN_PREFIX + jwtUtils.createRefreshToken(username);
    }

    @Test
    @DisplayName("로그인이 정상으로 되는 경우")
    void loginSuccessTest() throws Exception {
        // given
        LoginReq loginReq = LoginReq.builder()
                .username(username)
                .password(password)
                .build();
        String json = objectMapper.writeValueAsString(loginReq);

        // expected
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인이 실패하는 경우 - 틀린 비밀번호")
    void loginFailTestInvalidPassword() throws Exception {
        // given
        LoginReq loginReq = LoginReq.builder()
                .username(username)
                .password("invalid" + password)
                .build();
        String json = objectMapper.writeValueAsString(loginReq);

        // expected
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(HttpStatus.UNAUTHORIZED.value()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인이 실패하는 경우 - 잘못된 HTTP METHOD")
    void loginFailTestWrongHttpMethod() throws Exception {

        // given
        LoginReq loginReq = LoginReq.builder()
                .username(username)
                .password(password)
                .build();
        String json = objectMapper.writeValueAsString(loginReq);

        // expected
        mockMvc.perform(get(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    @DisplayName("엑세스 토큰을 리프레시 하는 경우")
    void refreshAccessTokenTest() throws Exception {
        // expected
        mockMvc.perform(post(TOKEN_REFRESH_URL)
                        .header(JwtProperties.HEADER_STRING, refreshToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 리프레시 토큰으로 엑세스 토큰을 리프레시 하는 경우")
    void refreshAccessTokenByInvalidRefreshTokenTest() throws Exception {
        // expected
        mockMvc.perform(post(TOKEN_REFRESH_URL)
                        .header(JwtProperties.HEADER_STRING, refreshToken + "invalid")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
