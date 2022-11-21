package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.UserCreateRequest;
import com.eunbinlib.api.exception.type.notfound.UserNotFoundException;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityExistsException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("게스트 유저 회원가입")
    void joinGuest() {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("username")
                .password("password")
                .build();

        // when
        userService.joinGuest(userCreateRequest);

        // then
        assertDoesNotThrow(() -> userRepository.findByUsername("username"));
    }

    @Test
    @DisplayName("이미 존재하는 게스트 유저를 회원가입")
    void joinGuestAlreadyExist() {
        // given
        Guest guest = Guest.builder()
                .username("username")
                .password("password")
                .build();
        userRepository.save(guest);

        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("username")
                .password("password")
                .build();

        // then
        assertThrows(EntityExistsException.class,
                () -> userService.joinGuest(userCreateRequest));
    }

    @Test
    @DisplayName("회원 유저 회원가입")
    void joinMember() {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("username")
                .password("password")
                .build();

        // when
        userService.joinMember(userCreateRequest);

        // then
        assertDoesNotThrow(() -> userRepository.findByUsername("username"));
    }

    @Test
    @DisplayName("이미 존재하는 회원 유저를 회원가입")
    void joinMemberAlreadyExist() {
        // given
        Member member = Member.builder()
                .username("username")
                .password("password")
                .build();
        userRepository.save(member);

        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("username")
                .password("password")
                .build();

        // then
        assertThrows(EntityExistsException.class,
                () -> userService.joinMember(userCreateRequest));
    }

    @Test
    @DisplayName("이름으로 유저 조회")
    void readMeByUsername() {
        // given
        String username = "username";
        String password = "password";

        Member member = Member.builder()
                .username(username)
                .password(password)
                .build();
        userRepository.save(member);

        // then
        User user = assertDoesNotThrow(
                () -> userService.readMeByUsername(username));

        Assertions.assertEquals(user.getUsername(), username);
        Assertions.assertEquals(user.getPassword(), password);
    }

    @Test
    @DisplayName("존재하지 않는 유저 조회")
    void readMeByUsernameNoUser() {
        // then
        assertThrows(UserNotFoundException.class,
                () -> userService.readMeByUsername("username"));
    }
}