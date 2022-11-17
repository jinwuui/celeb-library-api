package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.entity.user.Guest;
import com.eunbinlib.api.domain.entity.user.Member;
import com.eunbinlib.api.domain.entity.user.User;
import com.eunbinlib.api.domain.request.UserJoin;
import com.eunbinlib.api.exception.type.UserNotFoundException;
import com.eunbinlib.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User readMeByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public void joinMember(UserJoin userJoin) {

        boolean isExist = userRepository.existsByUsername(userJoin.getUsername());

        if (isExist) {
            throw new EntityExistsException("이미 존재하는 아이디입니다.");
        }

        Member member = Member.builder()
                .username(userJoin.getUsername())
                .password(userJoin.getPassword()) // TODO: 비밀번호 해싱 필요, BScrypt 인코딩 필요
                .build();

        userRepository.save(member);
    }

    public void joinGuest(UserJoin userJoin) {

        boolean isExist = userRepository.existsByUsername(userJoin.getUsername());

        if (isExist) {
            throw new EntityExistsException("이미 존재하는 게스트입니다.");
        }

        Guest guest = Guest.builder()
                .username(userJoin.getUsername())
                .password(userJoin.getPassword()) // TODO: 비밀번호 해싱 필요, BScrypt 인코딩 필요
                .build();

        userRepository.save(guest);
    }

//    public UserMeRes readMe() {
//
//    }
}
