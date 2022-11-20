package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.entity.user.Guest;
import com.eunbinlib.api.domain.entity.user.Member;
import com.eunbinlib.api.domain.entity.user.User;
import com.eunbinlib.api.domain.request.UserJoin;
import com.eunbinlib.api.exception.type.UserNotFoundException;
import com.eunbinlib.api.repository.post.PostRepository;
import com.eunbinlib.api.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityExistsException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
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
        UserJoin userJoin = UserJoin.builder()
                .username("username")
                .password("password")
                .build();

        // when
        userService.joinGuest(userJoin);

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

        UserJoin userJoin = UserJoin.builder()
                .username("username")
                .password("password")
                .build();

        // then
        assertThrows(EntityExistsException.class,
                () -> userService.joinGuest(userJoin));
    }

    @Test
    @DisplayName("회원 유저 회원가입")
    void joinMember() {
        // given
        UserJoin userJoin = UserJoin.builder()
                .username("username")
                .password("password")
                .build();

        // when
        userService.joinMember(userJoin);

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

        UserJoin userJoin = UserJoin.builder()
                .username("username")
                .password("password")
                .build();

        // then
        assertThrows(EntityExistsException.class,
                () -> userService.joinMember(userJoin));
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