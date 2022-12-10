package com.eunbinlib.api.controller;

import com.eunbinlib.api.domain.block.Block;
import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.GuestCreateRequest;
import com.eunbinlib.api.dto.request.MeUpdateRequest;
import com.eunbinlib.api.dto.request.MemberCreateRequest;
import com.eunbinlib.api.exception.type.application.notfound.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.transaction.Transactional;

import static com.eunbinlib.api.auth.data.AuthProperties.AUTHORIZATION_HEADER;
import static com.eunbinlib.api.auth.data.AuthProperties.TOKEN_PREFIX;
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

@Slf4j
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

            MemberCreateRequest request = new MemberCreateRequest(username, password, nickname);

            String json = objectMapper.writeValueAsString(request);

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

            MemberCreateRequest request = new MemberCreateRequest(username, password, null);

            String json = objectMapper.writeValueAsString(request);

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

            MemberCreateRequest request = new MemberCreateRequest(null, password, nickname);

            String json = objectMapper.writeValueAsString(request);

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

            MemberCreateRequest request = new MemberCreateRequest(username, null, nickname);

            String json = objectMapper.writeValueAsString(request);

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

            MemberCreateRequest request = new MemberCreateRequest(member.getUsername(), differentPassword, differentNickname);

            String json = objectMapper.writeValueAsString(request);

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

            GuestCreateRequest request = new GuestCreateRequest(username, password);

            String json = objectMapper.writeValueAsString(request);

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
            GuestCreateRequest request = new GuestCreateRequest(guest.getUsername(), differentPassword);

            String json = objectMapper.writeValueAsString(request);

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

            GuestCreateRequest request = new GuestCreateRequest(null, password);

            String json = objectMapper.writeValueAsString(request);

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

            GuestCreateRequest request = new GuestCreateRequest(username, null);

            String json = objectMapper.writeValueAsString(request);

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
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userType").value(member.getUserType()))
                    .andExpect(jsonPath("$.id").value(member.getId()))
                    .andExpect(jsonPath("$.nickname").value(member.getNickname().getValue()))
                    .andDo(print());
        }

        @Test
        @DisplayName("게스트 유저가 자신의 정보 조회하는 경우")
        void readMeByGuest() throws Exception {
            // given
            loginGuest();

            // expected
            mockMvc.perform(get("/api/users/me")
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + guestAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isForbidden())
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
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
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
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
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
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
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
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
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
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
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
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
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
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
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
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
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

            // expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/me")
                            .param("nickname", request.getNickname())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("유저 간 차단/차단해제")
    public class BlockAndUnblock {

        @Test
        @DisplayName("유저를 차단하는 경우")
        void blockUser() throws Exception {
            // given
            loginMember();
            Member target = getMember();

            // when
            mockMvc.perform(post("/api/users/{userId}/block", target.getId())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            assertThat(blockRepository.findAll().get(0).getBlocker().getId())
                    .isEqualTo(member.getId());
            assertThat(blockRepository.findAll().get(0).getBlocked().getId())
                    .isEqualTo(target.getId());
        }

        @Test
        @DisplayName("이미 차단된 유저를 차단하는 경우")
        void blockUserAlreadyBlocked() throws Exception {
            // given
            loginMember();
            Member target = getMember();
            blockRepository.save(Block.builder()
                    .blocker(member)
                    .blocked(target)
                    .build()
            );

            // when
            mockMvc.perform(post("/api/users/{userId}/block", target.getId())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            assertThat(blockRepository.findAll().get(0).getBlocker().getId())
                    .isEqualTo(member.getId());
            assertThat(blockRepository.findAll().get(0).getBlocked().getId())
                    .isEqualTo(target.getId());
        }

        @Test
        @DisplayName("존재하지 않는 유저를 차단하는 경우")
        void blockUserNotFoundBlocked() throws Exception {
            // given
            loginMember();
            Member target = getMember();

            // when
            mockMvc.perform(post("/api/users/{userId}/block", target.getId() + 100L)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                    )
                    .andExpect(status().isNotFound())
                    .andDo(print());

            // then
            assertThat(blockRepository.findAll().isEmpty())
                    .isTrue();
        }

        @Test
        @DisplayName("유저를 차단 해제하는 경우")
        void unblockUser() throws Exception {
            // given
            loginMember();
            Member target = getMember();
            blockRepository.save(Block.builder()
                    .blocker(member)
                    .blocked(target)
                    .build()
            );

            // when
            mockMvc.perform(post("/api/users/{userId}/unblock", target.getId())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            assertThat(blockRepository.findAll().isEmpty())
                    .isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 유저를 차단 해제하는 경우")
        void unblockUserNotFoundBlocked() throws Exception {
            // given
            loginMember();
            Member target = getMember();

            // expected
            mockMvc.perform(post("/api/users/{userId}/unblock", target.getId() + 100L)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                    )
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }

        @Test
        @DisplayName("차단되지 않은 유저를 차단 해제하는 경우")
        void unblockUserNotExist() throws Exception {
            // given
            loginMember();
            Member target = getMember();

            // expected
            mockMvc.perform(post("/api/users/{userId}/unblock", target.getId())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());
        }
    }
}