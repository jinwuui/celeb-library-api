package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.JwtProperties;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.entity.user.Guest;
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
import org.springframework.test.web.servlet.MockMvc;

import static com.eunbinlib.api.auth.data.JwtProperties.TOKEN_PREFIX;
import static com.eunbinlib.api.controller.UserController.JOIN_GUEST_URL;
import static com.eunbinlib.api.controller.UserController.JOIN_MEMBER_URL;
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
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtils jwtUtils;

    Member mockMember;

    String mockMemberToken;

    Guest mockGuest;

    String mockGuestToken;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clean() {

        mockMember = Member.builder()
                .username("mockMember")
                .nickname("mockMember")
                .password("mockPassword")
                .build();

        mockMemberToken = createAccessToken(mockMember.getUserType(), mockMember.getUsername());


        mockGuest = Guest.builder()
                .username("mockGuest")
                .password("mockPassword")
                .build();

        mockGuestToken = createAccessToken(mockGuest.getUserType(), mockGuest.getUsername());

        userRepository.deleteAll();
    }

    private String createAccessToken(String userType, String username) {
        return TOKEN_PREFIX + jwtUtils.createAccessToken(userType, username);
    }


    @Test
    @DisplayName("회원 유저가 자신의 정보 조회하는 경우")
    void readMeByMember() throws Exception {
        // given
        Member savedMember = userRepository.save(mockMember);

        // expected
        mockMvc.perform(get("/api/users/me")
                        .header(JwtProperties.HEADER_STRING, mockMemberToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userType").value(savedMember.getUserType()))
                .andExpect(jsonPath("$.id").value(savedMember.getId()))
                .andExpect(jsonPath("$.username").value(savedMember.getUsername()))
                .andDo(print());
    }

    @Test
    @DisplayName("게스트 유저가 자신의 정보 조회하는 경우")
    void readMeByGuest() throws Exception {
        // given
        Guest savedGuest = userRepository.save(mockGuest);

        // expected
        mockMvc.perform(get("/api/users/me")
                        .header(JwtProperties.HEADER_STRING, mockGuestToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userType").value(savedGuest.getUserType()))
                .andExpect(jsonPath("$.id").value(savedGuest.getId()))
                .andExpect(jsonPath("$.username").value(savedGuest.getUsername()))
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
        mockMvc.perform(post(JOIN_MEMBER_URL)
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
        assertEquals(findUser.getUserType(), "member");
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
        mockMvc.perform(post(JOIN_GUEST_URL)
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
        assertEquals(findUser.getUserType(), "guest");
    }
}