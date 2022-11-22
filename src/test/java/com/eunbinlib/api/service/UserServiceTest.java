package com.eunbinlib.api.service;

import com.eunbinlib.api.ApplicationTest;
import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.MeUpdateRequest;
import com.eunbinlib.api.dto.request.UserCreateRequest;
import com.eunbinlib.api.exception.type.EunbinlibIllegalArgumentException;
import com.eunbinlib.api.exception.type.notfound.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
class UserServiceTest extends ApplicationTest {

    private static Integer SEQ = 0;

    @Autowired
    UserService userService;

    String username = "username";
    String password = "password";
    String nickname = "nickname";

    Member getMember() {
        ++SEQ;
        return Member.builder()
                .username(username + SEQ)
                .password(password + SEQ)
                .nickname(nickname + SEQ)
                .build();
    }

    Member getSavedMember() {
        return userRepository.save(getMember());
    }

    Guest getGuest() {
        ++SEQ;
        return Guest.builder()
                .username(username + SEQ)
                .password(password + SEQ)
                .build();
    }

    Guest getSavedGuest() {
        return userRepository.save(getGuest());
    }


    @Nested
    @DisplayName("유저 생성")
    class Create {

        @Test
        @DisplayName("게스트 유저 가입")
        void createGuest() {
            // given
            UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                    .username(username)
                    .password(password)
                    .build();

            // expected
            assertDoesNotThrow(() -> userService.createGuest(userCreateRequest));
        }

        @Test
        @DisplayName("이미 존재하는 게스트 유저를 회원가입")
        void createGuestAlreadyExist() {
            // given
            Guest guest = getSavedGuest();

            UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                    .username(guest.getUsername())
                    .password(guest.getPassword())
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
            Member member = getSavedMember();

            UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                    .username(member.getUsername())
                    .password(member.getPassword())
                    .nickname(member.getNickname().getValue())
                    .build();

            // expected
            assertThatThrownBy(() -> userService.createMember(userCreateRequest))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("유저 조회")
    class Read {

        @Test
//        @Transactional
        @DisplayName("이름으로 유저 조회")
        void readMeByUsername() {
            // given
            Member member = getSavedMember();

            // when
            User user = userService.readMeByUsername(member.getUsername());

            // expected
//            assertThat(user)
//                    .isEqualTo(member);
            assertThat(user.getId())
                    .isEqualTo(member.getId());
        }

        @Test
        @DisplayName("존재하지 않는 유저 조회")
        void readMeByUsernameNoUser() {
            // expected
            assertThatThrownBy(() -> userService.readMeByUsername(username))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("프로필 수정")
    public class UpdateMe {

        @Test
//        @Transactional(value = Transactional.TxType.REQUIRES_NEW)
        @DisplayName("자신의 프로필 수정 - 닉네임만 변경")
        void updateMeNickname() {
            // given
            Member member = getSavedMember();
            MeUpdateRequest request = new MeUpdateRequest("수정 닉네임", null);

            log.info("================member================member================member================member================member1");
            // when
            userService.updateMe(member.getId(), request);

            log.info("================member================member================member================member================member2");
            // then
            Member findMember = memberRepository.findById(member.getId())
                    .orElseThrow(UserNotFoundException::new);

            log.info("================member================member================member================member================member3");
            assertThat(findMember.getNickname().getValue())
                    .isEqualTo(request.getNickname());
            log.info("================member================member================member================member================member4");
        }

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임이 너무 짧을 때")
        void updateMeNicknameTooShort() {
            // given
            Member member = getSavedMember();
            MeUpdateRequest request = new MeUpdateRequest("닉", null);

            // expected
            assertThatThrownBy(() -> userService.updateMe(member.getId(), request))
                    .isInstanceOf(EunbinlibIllegalArgumentException.class);
        }

        @Test
        @DisplayName("자신의 프로필 수정 - 닉네임이 너무 길 때")
        void updateMeNicknameTooLong() {
            // given
            Member member = getSavedMember();
            MeUpdateRequest request = new MeUpdateRequest("가나다라마바사아자차카타파하", null);

            // expected
            assertThatThrownBy(() -> userService.updateMe(member.getId(), request))
                    .isInstanceOf(EunbinlibIllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 닉네임으로 수정할 때")
        void updateMeNicknameBlank() {
            // given
            Member member = getSavedMember();
            MeUpdateRequest request = new MeUpdateRequest(" ", null);

            // expected
            assertThatThrownBy(() -> userService.updateMe(member.getId(), request))
                    .isInstanceOf(EunbinlibIllegalArgumentException.class);
        }

        @Test
        @DisplayName("특수문자가 포함된 닉네임으로 수정할 때")
        void updateMeNicknameInvalidCharacter() {
            // given
            Member member = getSavedMember();
            MeUpdateRequest request = new MeUpdateRequest("!@#$" + nickname, null);

            // expected
            assertThatThrownBy(() -> userService.updateMe(member.getId(), request))
                    .isInstanceOf(EunbinlibIllegalArgumentException.class);
        }

        @Test
        @Transactional
        @DisplayName("자신의 프로필 수정 - 프로필 이미지만 변경")
        void updateMeProfileImageFile() {
            // given
            Member member = getSavedMember();
            MockMultipartFile profileImageFile = new MockMultipartFile(
                    "images",
                    "test.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "<<jpg data>>".getBytes()
            );

            MeUpdateRequest request = new MeUpdateRequest(null, profileImageFile);

            // when
            userService.updateMe(member.getId(), request);

            // expected
            BaseImageFile baseImageFile = member.getProfileImageFile().getBaseImageFile();

            assertThat(baseImageFile)
                    .isNotNull();
            assertThat(baseImageFile.getOriginalFilename())
                    .isEqualTo(profileImageFile.getOriginalFilename());
        }

        @Test
        @DisplayName("잘못된 타입의 파일을 프로필에 넣은 경우")
        void updateMeInvalidFileContentType() throws IOException {
            // given
            Member member = getSavedMember();
            MockMultipartFile profileImageFile = new MockMultipartFile(
                    "images",
                    "test.jpg",
                    MediaType.TEXT_PLAIN_VALUE,
                    "this is text".getBytes()
            );

            MeUpdateRequest request = new MeUpdateRequest(null, profileImageFile);

            // when
            userService.updateMe(member.getId(), request);

            // then
            assertThat(member.getProfileImageFile())
                    .isNull();
        }

        @Test
        @Transactional
        @DisplayName("자신의 프로필 수정 - 닉네임 & 프로필 모두 변경")
        void updateMeNicknameAndProfileImageFile() throws IOException {
            // given
            Member member = getSavedMember();
            MockMultipartFile profileImageFile = new MockMultipartFile(
                    "images",
                    "test2.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "<<jpg data>>".getBytes()
            );

            MeUpdateRequest request = new MeUpdateRequest("수정된 닉네임", profileImageFile);

            // when
            userService.updateMe(member.getId(), request);

            // then
            Member findMember = memberRepository.findById(member.getId())
                    .orElseThrow(IllegalArgumentException::new);
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
        void updateMeNoUpdateInfo() {
            // given
            Member member = getSavedMember();
            String oldNickname = member.getNickname().getValue();
            MeUpdateRequest request = new MeUpdateRequest(null, null);

            // when
            userService.updateMe(member.getId(), request);

            // then
            assertThat(member.getProfileImageFile())
                    .isNull();
            assertThat(member.getNickname().getValue())
                    .isEqualTo(oldNickname);
        }
    }
}