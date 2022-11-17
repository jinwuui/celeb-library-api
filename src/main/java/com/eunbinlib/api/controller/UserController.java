package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.domain.entity.user.Member;
import com.eunbinlib.api.domain.entity.user.User;
import com.eunbinlib.api.domain.request.UserJoin;
import com.eunbinlib.api.domain.response.UserMeRes;
import com.eunbinlib.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    public static final String JOIN_MEMBER_URL = "/api/users/member";
    public static final String JOIN_GUEST_URL ="/api/users/guest";

    private final UserService userService;

    @GetMapping("/me")
    public UserMeRes readMe(UserSession userSession) {
        User user = userService.readMeByUsername(userSession.getUsername());

        String imageUrl = getImageUrl(user);

        return UserMeRes.builder()
                .userType(user.getDiscriminatorValue())
                .id(user.getId())
                .username(user.getUsername())
                .imageUrl(imageUrl)
                .build();
    }

    @PostMapping("/member")
    public void joinMember(@RequestBody @Valid UserJoin userJoin) {
        userService.joinMember(userJoin);
    }

    @PostMapping("/guest")
    public void joinGuest(@RequestBody @Valid UserJoin userJoin) {
        userService.joinGuest(userJoin);
    }

    private String getImageUrl(User user) {
        String imageUrl = "default_profile";

        if (Objects.equals(user.getClass(), Member.class)) {
            Member member = (Member) user;

            if (member.getImageUrl() != null) {
                imageUrl = member.getImageUrl();
            }
        }

        return imageUrl;
    }
}
