package com.eunbinlib.api.controller;

import com.eunbinlib.api.domain.entity.user.Member;
import com.eunbinlib.api.domain.entity.user.User;
import com.eunbinlib.api.domain.request.UserJoin;
import com.eunbinlib.api.domain.response.UserMeRes;
import com.eunbinlib.api.security.model.CustomUserDetails;
import com.eunbinlib.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserMeRes readMe(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();

        String imageUrl = getImageUrl(user);

        return UserMeRes.builder()
                .userType(user.getDiscriminatorValue())
                .id(user.getId())
                .username(user.getUsername())
                .imageUrl(imageUrl)
                .build();
    }


    @PostMapping()
    public void join(@RequestBody @Valid UserJoin userJoin) {
        userService.join(userJoin);
    }


    private String getImageUrl(User user) {
        if (Objects.equals(user.getClass(), Member.class)) {
            Member member = (Member) user;

            return member.getImageUrl();
        } else {
            return "default_profile";
        }
    }


}
