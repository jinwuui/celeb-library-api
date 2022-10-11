package com.eunbinlib.api.controller;

import com.eunbinlib.api.domain.request.UserJoin;
import com.eunbinlib.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

//    @GetMapping("/api/users")
//    public   read() {
//        // accessToken으로 특정 유저의 정보를 조회
//
//    }

    @PostMapping()
    public void join(@RequestBody @Valid UserJoin userJoin) {
        userService.join(userJoin);
    }

}
