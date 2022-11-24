package com.eunbinlib.api.controller;

import com.eunbinlib.api.ControllerTest;
import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.GuestCreateRequest;
import com.eunbinlib.api.dto.request.MeUpdateRequest;
import com.eunbinlib.api.dto.request.MemberCreateRequest;
import com.eunbinlib.api.exception.type.notfound.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.transaction.Transactional;

import static com.eunbinlib.api.auth.data.JwtProperties.HEADER_AUTHORIZATION;
import static com.eunbinlib.api.auth.data.JwtProperties.TOKEN_PREFIX;
import static com.eunbinlib.api.controller.UserController.JOIN_GUEST_URL;
import static com.eunbinlib.api.controller.UserController.JOIN_MEMBER_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTest {

    public static final String MEMBER_TYPE = "member";
    public static final String GUEST_TYPE = "guest";

    @Nested
    @DisplayName("사용자 생성")
    public class Create {

        @Test
        @DisplayName("회원 유저 가입")
        void createMember() throws Exception {
            // given
            String username = "testId";
            String password = "testPw";
            String nickname = "tester";

            MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                    .username(username)
                    .password(password)
                    .nickname(nickname)
                    .build();

            String json = objectMapper.writeValueAsString(memberCreateRequest);

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
        @DisplayName("닉네임 없이 회원 유저 가입")
        void createMemberNoNickname() throws Exception {
            // given
            String username = "testId";
            String password = "testPw";

            MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                    .username(username)
                    .password(password)
                    .build();

            String json = objectMapper.writeValueAsString(memberCreateRequest);

            // when
            mockMvc.perform(post(JOIN_MEMBER_URL)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("아이디 없이 회원 유저 가입")
        void createMemberNoUsername() throws Exception {
            // given
            String password = "testPw";
            String nickname = "tester";

            MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                    .password(password)
                    .nickname(nickname)
                    .build();

            String json = objectMapper.writeValueAsString(memberCreateRequest);

            // when
            mockMvc.perform(post(JOIN_MEMBER_URL)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("비밀번호 없이 회원 유저 가입")
        void createMemberNoPassword() throws Exception {
            // given
            String username = "testId";
            String nickname = "tester";

            MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                    .username(username)
                    .nickname(nickname)
                    .build();

            String json = objectMapper.writeValueAsString(memberCreateRequest);

            // when
            mockMvc.perform(post(JOIN_MEMBER_URL)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("회원 가입 시, 중복된 아이디로 가입하는 경우")
        void createMemberDuplicatedUsername() throws Exception {
            // given
            Member member = getMember();

            String differentPassword = member.getPassword() + "2";
            String differentNickname = member.getNickname() + "2";

            MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                    .username(member.getUsername())
                    .password(differentPassword)
                    .nickname(differentNickname)
                    .build();

            String json = objectMapper.writeValueAsString(memberCreateRequest);

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
        void createGuest() throws Exception {
            // given
            GuestCreateRequest guestCreateRequest = GuestCreateRequest.builder()
                    .username(username)
                    .password(password)
                    .build();

            String json = objectMapper.writeValueAsString(guestCreateRequest);

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
        void createGuestDuplicatedUsername() throws Exception {
            // given
            Guest guest = getGuest();

            String differentPassword = guest.getPassword() + "2";
            GuestCreateRequest guestCreateRequest = GuestCreateRequest.builder()
                    .username(guest.getUsername())
                    .password(differentPassword)
                    .build();

            String json = objectMapper.writeValueAsString(guestCreateRequest);

            // expected
            mockMvc.perform(post(JOIN_GUEST_URL)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("아이디 없이 게스트 유저 가입")
        void createGuestNoUsername() throws Exception {
            // given
            String password = "testPw";

            GuestCreateRequest guestCreateRequest = GuestCreateRequest.builder()
                    .password(password)
                    .build();

            String json = objectMapper.writeValueAsString(guestCreateRequest);

            // when
            mockMvc.perform(post(JOIN_GUEST_URL)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("비밀번호 없이 게스트 유저 가입")
        void createGuestNoPassword() throws Exception {
            // given
            String username = "testId";

            GuestCreateRequest guestCreateRequest = GuestCreateRequest.builder()
                    .username(username)
                    .build();

            String json = objectMapper.writeValueAsString(guestCreateRequest);

            // when
            mockMvc.perform(post(JOIN_GUEST_URL)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("사용자 자신의 정보 조회")
    public class Read {

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
    }

    @Nested
    @DisplayName("프로필 수정")
    public class UpdateMe {

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임만 변경")
        void updateMeNickname() throws Exception {
            // given
            loginMember();
            MeUpdateRequest request = new MeUpdateRequest("수정 닉네임", null);

            // when
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/me")
                            .param("nickname", request.getNickname())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            Member findMember = memberRepository.findById(member.getId())
                    .orElseThrow(UserNotFoundException::new);

            assertThat(findMember.getNickname().getValue())
                    .isEqualTo(request.getNickname());
        }

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임이 너무 짧을 때")
        void updateMeNicknameTooShort() throws Exception {
            // given
            loginMember();
            MeUpdateRequest request = new MeUpdateRequest("닉", null);

            // expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/me")
                            .param("nickname", request.getNickname())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임이 너무 길 때")
        void updateMeNicknameTooLong() throws Exception {
            // given
            loginMember();
            MeUpdateRequest request = new MeUpdateRequest("가나다라마바사아자차카타파하", null);

            // expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/me")
                            .param("nickname", request.getNickname())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("공백 닉네임으로 수정할 때")
        void updateMeNicknameBlank() throws Exception {
            loginMember();
            MeUpdateRequest request = new MeUpdateRequest(" ", null);

            // expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/me")
                            .param("nickname", request.getNickname())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("특수문자가 포함된 닉네임으로 수정할 때")
        void updateMeNicknameInvalidCharacter() throws Exception {
            // given
            loginMember();
            MeUpdateRequest request = new MeUpdateRequest("!@#$" + nickname, null);

            // expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/me")
                            .param("nickname", request.getNickname())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @Transactional
        @DisplayName("자신의 프로필 수정 - 프로필 이미지만 변경")
        void updateMeProfileImageFile() throws Exception {
            // given
            loginMember();

            MockMultipartFile profileImageFile = new MockMultipartFile(
                    "profileImageFile",
                    "test.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "<<jpg data>>".getBytes()
            );
            MeUpdateRequest request = new MeUpdateRequest(null, profileImageFile);

            // expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/me")
                            .file((MockMultipartFile) request.getProfileImageFile())
                            .param("nickname", request.getNickname())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            Member findMember = memberRepository.findById(member.getId()).orElseThrow(IllegalArgumentException::new);
            BaseImageFile baseImageFile = findMember.getProfileImageFile().getBaseImageFile();

            assertThat(baseImageFile)
                    .isNotNull();
            assertThat(baseImageFile.getOriginalFilename())
                    .isEqualTo(profileImageFile.getOriginalFilename());
        }

        @Test
        @DisplayName("잘못된 타입의 파일을 프로필에 넣은 경우")
        void updateMeInvalidFileContentType() throws Exception {
            // given
            loginMember();
            MockMultipartFile profileImageFile = new MockMultipartFile(
                    "profileImageFile",
                    "test.jpg",
                    MediaType.TEXT_PLAIN_VALUE,
                    "this is text".getBytes()
            );

            MeUpdateRequest request = new MeUpdateRequest(null, profileImageFile);

            // expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/me")
                            .file((MockMultipartFile) request.getProfileImageFile())
                            .param("nickname", request.getNickname())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @Transactional
        @DisplayName("자신의 프로필 수정 - 닉네임 & 프로필 모두 변경")
        void updateMeNicknameAndProfileImageFile() throws Exception {
            // given
            loginMember();
            MockMultipartFile profileImageFile = new MockMultipartFile(
                    "profileImageFile",
                    "test2.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "<<jpg data>>".getBytes()
            );

            MeUpdateRequest request = new MeUpdateRequest("수정된 닉네임", profileImageFile);

            // when
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/me")
                            .file((MockMultipartFile) request.getProfileImageFile())
                            .param("nickname", request.getNickname())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            BaseImageFile baseImageFile = member.getProfileImageFile()
                    .getBaseImageFile();

            assertThat(member.getNickname().getValue())
                    .isEqualTo("수정된 닉네임");
            assertThat(member.getNickname().getValue())
                    .isEqualTo("수정된 닉네임");

            // then
            assertThat(baseImageFile.getStoredFilename())
                    .isNotBlank();
            assertThat(baseImageFile.getOriginalFilename())
                    .isEqualTo(profileImageFile.getOriginalFilename());
            assertThat(baseImageFile.getContentType())
                    .isEqualTo(MediaType.IMAGE_JPEG_VALUE);
            assertThat(baseImageFile.getByteSize())
                    .isEqualTo(profileImageFile.getBytes().length);

        }

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임 & 프로필 둘 다 없는 경우")
        void updateMeNoUpdateInfo() throws Exception {
            // given
            loginMember();
            MeUpdateRequest request = new MeUpdateRequest(null, null);

            // when
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/me")
                            .param("nickname", request.getNickname())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}