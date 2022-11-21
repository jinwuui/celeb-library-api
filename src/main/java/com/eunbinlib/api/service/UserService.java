package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.UserCreateRequest;
import com.eunbinlib.api.exception.type.notfound.UserNotFoundException;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    public void joinMember(UserCreateRequest userCreateRequest) {

        try {
            Member member = Member.builder()
                    .username(userCreateRequest.getUsername())
                    .password(userCreateRequest.getPassword()) // TODO: 비밀번호 해싱 필요, BScrypt 인코딩 필요
                    .build();

            userRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException("중복되는 아이디 입니다.", e);
        }
    }

    public void joinGuest(UserCreateRequest userCreateRequest) {

        try {
            Guest guest = Guest.builder()
                    .username(userCreateRequest.getUsername())
                    .password(userCreateRequest.getPassword()) // TODO: 비밀번호 해싱 필요, BScrypt 인코딩 필요
                    .build();

            userRepository.save(guest);
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException("중복되는 아이디 입니다.", e);
        }
    }

//    public UserMeRes readMe() {
//
//    }
}
