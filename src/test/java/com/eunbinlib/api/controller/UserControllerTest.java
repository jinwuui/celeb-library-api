package com.eunbinlib.api.controller;

import com.eunbinlib.api.ControllerTest;
import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.UserCreateRequest;
import com.eunbinlib.api.exception.type.notfound.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.eunbinlib.api.auth.data.JwtProperties.HEADER_AUTHORIZATION;
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

class UserControllerTest extends ControllerTest {

    public static final String MEMBER_TYPE = "member";
    public static final String GUEST_TYPE = "guest";

    @Test
    @DisplayName("회원 유저가 자신의 정보 조회하는 경우")
    void readMeByMember() throws Exception {
        // given
        loginMember();

        // expected
        mockMvc.perform(get("/api/users/me")
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userType").value(member.getUserType()))
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.username").value(member.getUsername()))
                .andDo(print());
    }

    @Test
    @DisplayName("게스트 유저가 자신의 정보 조회하는 경우")
    void readMeByGuest() throws Exception {
        // given
        loginGuest();

        // expected
        mockMvc.perform(get("/api/users/me")
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + guestAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userType").value(guest.getUserType()))
                .andExpect(jsonPath("$.id").value(guest.getId()))
                .andExpect(jsonPath("$.username").value(guest.getUsername()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 유저 가입")
    void joinMember() throws Exception {
        // given
        String username = "testId";
        String password = "testPw";
        String nickname = "tester";

        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();

        String json = objectMapper.writeValueAsString(userCreateRequest);

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
        assertEquals(findUser.getUserType(), MEMBER_TYPE);
    }

    @Test
    @DisplayName("회원 가입 시, 중복된 아이디로 가입하는 경우")
    void joinMemberDuplicatedUsername() throws Exception {
        // given
        Member member = getMember();

        String differentPassword = member.getPassword() + "2";
        String differentNickname = member.getNickname() + "2";

        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(member.getUsername())
                .password(differentPassword)
                .nickname(differentNickname)
                .build();

        String json = objectMapper.writeValueAsString(userCreateRequest);

        // expected
        mockMvc.perform(post(JOIN_MEMBER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("게스트 유저 가입")
    void joinGuest() throws Exception {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .build();

        String json = objectMapper.writeValueAsString(userCreateRequest);

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
        assertEquals(findUser.getUserType(), GUEST_TYPE);
    }

    @Test
    @DisplayName("게스트 가입 시, 중복된 아이디로 가입하는 경우")
    void joinGuestDuplicatedUsername() throws Exception {
        // given
        Guest guest = getGuest();

        String differentPassword = guest.getPassword() + "2";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(guest.getUsername())
                .password(differentPassword)
                .build();

        String json = objectMapper.writeValueAsString(userCreateRequest);

        // expected
        mockMvc.perform(post(JOIN_GUEST_URL)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}