package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.JwtProperties;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.entity.user.Member;
import com.eunbinlib.api.domain.entity.user.User;
import com.eunbinlib.api.domain.request.UserJoin;
import com.eunbinlib.api.exception.type.UserNotFoundException;
import com.eunbinlib.api.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtils jwtUtils;

    String token;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clean() {
        token = JwtProperties.TOKEN_PREFIX + jwtUtils.createAccessToken("member");
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("자신의 정보 조회")
    void readMe() throws Exception {
        // given
        Member member = Member.builder()
                .username("member")
                .password("password")
                .nickname("test")
                .build();

        Member savedMember = userRepository.save(member);

        // expected
        mockMvc.perform(get("/api/users/me")
                        .header(JwtProperties.HEADER_STRING, token)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userType").value(savedMember.getDiscriminatorValue()))
                .andExpect(jsonPath("$.id").value(savedMember.getId()))
                .andExpect(jsonPath("$.username").value(savedMember.getUsername()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 유저 가입")
    void joinMember() throws Exception {
        // given
        String username = "testId";
        String password = "testPw";
        UserJoin userJoin = UserJoin.builder()
                .username(username)
                .password(password)
                .build();

        String json = objectMapper.writeValueAsString(userJoin);

        // when
        mockMvc.perform(post("/api/users/member")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        User findUser = assertDoesNotThrow(
                () -> userRepository.findByUsername(username)
                        .orElseThrow(UserNotFoundException::new)
        );

        assertEquals(findUser.getUsername(), username);
        assertEquals(findUser.getDiscriminatorValue(), "member");
    }

    @Test
    @DisplayName("게스트 유저 가입")
    void joinGuest() throws Exception {
        // given
        String username = "testId";
        String password = "testPw";
        UserJoin userJoin = UserJoin.builder()
                .username(username)
                .password(password)
                .build();

        String json = objectMapper.writeValueAsString(userJoin);

        // when
        mockMvc.perform(post("/api/users/guest")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        User findUser = assertDoesNotThrow(
                () -> userRepository.findByUsername(username)
                        .orElseThrow(UserNotFoundException::new)
        );

        assertEquals(findUser.getUsername(), username);
        assertEquals(findUser.getDiscriminatorValue(), "guest");
    }
}