package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.domain.repository.user.MemberRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.MeUpdateRequest;
import com.eunbinlib.api.dto.request.UserCreateRequest;
import com.eunbinlib.api.exception.type.EunbinlibIllegalArgumentException;
import com.eunbinlib.api.exception.type.notfound.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostImageFileRepository postImageFileRepository;

    String username = "username";
    String password = "password";
    String nickname = "nickname";

    @BeforeEach
    void beforeEach() {
        postImageFileRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("게스트 유저 가입")
    void createGuest() {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username + "guest")
                .password(password)
                .build();

        // expected
        assertDoesNotThrow(() -> userService.createGuest(userCreateRequest));
    }

    @Test
    @DisplayName("이미 존재하는 게스트 유저를 회원가입")
    void createGuestAlreadyExist() {
        // given
        Guest guest = Guest.builder()
                .username(username)
                .password(password)
                .build();
        userRepository.save(guest);

        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .build();

        // expected
        assertThatThrownBy(() -> userService.createGuest(userCreateRequest))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("회원 유저 가입")
    void createMember() {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();

        // expected
        assertDoesNotThrow(() -> userService.createMember(userCreateRequest));
    }

    @Test
    @DisplayName("공백 닉네임으로 회원 유저 가입")
    void createMemberNicknameBlank() {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .nickname("  ")
                .build();

        // expected
        assertThatThrownBy(() -> userService.createMember(userCreateRequest))
                .isInstanceOf(EunbinlibIllegalArgumentException.class);
    }

    @Test
    @DisplayName("너무 짧은 닉네임으로 회원 유저 가입")
    void createMemberNicknameTooShort() {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .nickname("닉")
                .build();

        // expected
        assertThatThrownBy(() -> userService.createMember(userCreateRequest))
                .isInstanceOf(EunbinlibIllegalArgumentException.class);
    }

    @Test
    @DisplayName("너무 긴 닉네임으로 회원 유저 가입")
    void createMemberNicknameTooLong() {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .nickname("가나다라마바사아자차카타파하")
                .build();

        // expected
        assertThatThrownBy(() -> userService.createMember(userCreateRequest))
                .isInstanceOf(EunbinlibIllegalArgumentException.class);
    }

    @Test
    @DisplayName("특수문자가 포함된 닉네임으로 회원 유저 가입")
    void createMemberNicknameInvalidCharacter() {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .nickname("@#$%" + nickname)
                .build();

        // expected
        assertThatThrownBy(() -> userService.createMember(userCreateRequest))
                .isInstanceOf(EunbinlibIllegalArgumentException.class);
    }

    @Test
    @DisplayName("중복 닉네임으로 회원 유저 가입")
    void createMemberDuplicatedNickname() {
        // given
        UserCreateRequest request1 = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();
        userService.createMember(request1);

        UserCreateRequest request2 = UserCreateRequest.builder()
                .username(username + "2")
                .password(password)
                .nickname(nickname)
                .build();

        // expected
        assertThatThrownBy(() -> userService.createMember(request2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("닉네임 없이 회원 유저 가입")
    void createMemberWithoutNickname() {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .build();

        // expected
        assertThatThrownBy(() -> userService.createMember(userCreateRequest))
                .isInstanceOf(EunbinlibIllegalArgumentException.class);
    }

    @Test
    @DisplayName("이미 존재하는 회원 유저를 회원가입")
    void createMemberAlreadyExist() {
        // given
        Member member = Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();
        userRepository.save(member);

        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();

        // expected
        assertThatThrownBy(() -> userService.createMember(userCreateRequest))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("이름으로 유저 조회")
    void readMeByUsername() {
        // given
        Member member = Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();
        userRepository.save(member);

        // when
        User user = userService.readMeByUsername(username);

        // expected
        assertThat(user.getUsername())
                .isEqualTo(username);
        assertThat(user.getPassword())
                .isEqualTo(password);
    }

    @Test
    @DisplayName("존재하지 않는 유저 조회")
    void readMeByUsernameNoUser() {
        // expected
        assertThatThrownBy(() -> userService.readMeByUsername(username))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Nested
    @DisplayName("프로필 수정")
    public class UpdateMe {

        Member mockMember;

        @BeforeEach
        void updateBeforeEach() {
            mockMember = Member.builder()
                    .username(username)
                    .password(password)
                    .nickname(nickname)
                    .build();
            userRepository.save(mockMember);
        }

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임만 변경")
        void updateMeNickname() {
            // given
            MeUpdateRequest request = new MeUpdateRequest("수정 닉네임", null);

            // when
            userService.updateMe(mockMember.getId(), request);

            // then
            Member findMember = memberRepository.findById(mockMember.getId())
                    .orElseThrow(UserNotFoundException::new);

            assertThat(findMember.getNickname().getValue())
                    .isEqualTo(request.getNickname());
        }

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임이 너무 짧을 때")
        void updateMeNicknameTooShort() {
            // given
            MeUpdateRequest request = new MeUpdateRequest("닉", null);

            // expected
            assertThatThrownBy(() -> userService.updateMe(mockMember.getId(), request))
                    .isInstanceOf(EunbinlibIllegalArgumentException.class);
        }

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임이 너무 길 때")
        void updateMeNicknameTooLong() {
            // given
            MeUpdateRequest request = new MeUpdateRequest("가나다라마바사아자차카타파하", null);

            // expected
            assertThatThrownBy(() -> userService.updateMe(mockMember.getId(), request))
                    .isInstanceOf(EunbinlibIllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 닉네임으로 수정할 때")
        void updateMeNicknameBlank() {
            // given
            MeUpdateRequest request = new MeUpdateRequest(" ", null);

            // expected
            assertThatThrownBy(() -> userService.updateMe(mockMember.getId(), request))
                    .isInstanceOf(EunbinlibIllegalArgumentException.class);
        }

        @Test
        @DisplayName("특수문자가 포함된 닉네임으로 수정할 때")
        void updateMeNicknameInvalidCharacter() {
            // given
            MeUpdateRequest request = new MeUpdateRequest("!@#$" + nickname, null);

            // expected
            assertThatThrownBy(() -> userService.updateMe(mockMember.getId(), request))
                    .isInstanceOf(EunbinlibIllegalArgumentException.class);
        }

        @Test
        @DisplayName("자신의 프로필 수정 - 프로필 이미지만 변경")
        void updateMeProfileImageFile() throws IOException {
            // given
            MockMultipartFile profileImageFile = new MockMultipartFile(
                    "images",
                    "test.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "<<jpg data>>".getBytes()
            );

            MeUpdateRequest request = new MeUpdateRequest(null, profileImageFile);

            // when
            userService.updateMe(mockMember.getId(), request);

            BaseImageFile baseImageFile = mockMember.getProfileImageFile()
                    .getBaseImageFile();

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
        @DisplayName("잘못된 타입의 파일을 프로필에 넣은 경우")
        void updateMeInvalidFileContentType() throws IOException {
            // given
            MockMultipartFile profileImageFile = new MockMultipartFile(
                    "images",
                    "test.jpg",
                    MediaType.TEXT_PLAIN_VALUE,
                    "this is text".getBytes()
            );

            MeUpdateRequest request = new MeUpdateRequest(null, profileImageFile);

            // when
            userService.updateMe(mockMember.getId(), request);

            // then
            assertThat(mockMember.getProfileImageFile())
                    .isNull();
        }

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임 & 프로필 모두 변경")
        void updateMeNicknameAndProfileImageFile() throws IOException {
            // given
            MockMultipartFile profileImageFile = new MockMultipartFile(
                    "images",
                    "test2.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "<<jpg data>>".getBytes()
            );

            MeUpdateRequest request = new MeUpdateRequest("수정된 닉네임", profileImageFile);

            // when
            userService.updateMe(mockMember.getId(), request);

            // then
            BaseImageFile baseImageFile = mockMember.getProfileImageFile()
                    .getBaseImageFile();

            // then
            assertThat(baseImageFile.getStoredFilename())
                    .isNotBlank();
            assertThat(baseImageFile.getOriginalFilename())
                    .isEqualTo(profileImageFile.getOriginalFilename());
            assertThat(baseImageFile.getContentType())
                    .isEqualTo(MediaType.IMAGE_JPEG_VALUE);
            assertThat(baseImageFile.getByteSize())
                    .isEqualTo(profileImageFile.getBytes().length);
            assertThat(mockMember.getNickname().getValue())
                    .isEqualTo("수정된 닉네임");
        }

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임 & 프로필 둘 다 없는 경우")
        void updateMeNoUpdateInfo() {
            // given
            MeUpdateRequest request = new MeUpdateRequest(null, null);

            // when
            userService.updateMe(mockMember.getId(), request);

            // then
            assertThat(mockMember.getProfileImageFile())
                    .isNull();
            assertThat(mockMember.getNickname().getValue())
                    .isEqualTo("nickname");
        }
    }
}