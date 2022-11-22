package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.auth.utils.AuthUtils;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.domain.user.User;
import com.eunbinlib.api.dto.request.MeUpdateRequest;
import com.eunbinlib.api.dto.request.UserCreateRequest;
import com.eunbinlib.api.dto.response.UserMeResponse;
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

    public static final String JOIN_MEMBER_URL = "/api/users/members";
    public static final String JOIN_GUEST_URL ="/api/users/guests";

    private final UserService userService;

    @GetMapping("/me")
    public UserMeResponse readMe(UserSession userSession) {
        User user = userService.readMeByUsername(userSession.getUsername());

        String imageUrl = getImageUrl(user);

        return UserMeResponse.builder()
                .userType(user.getUserType())
                .id(user.getId())
                .username(user.getUsername())
                .imageUrl(imageUrl)
                .build();
    }

    @PostMapping("/members")
    public void createMember(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        userService.createMember(userCreateRequest);
    }

    @PostMapping("/guests")
    public void createGuest(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        userService.createGuest(userCreateRequest);
    }

    @PatchMapping("/me")
    public void updateMe(UserSession userSession, @RequestBody @Valid MeUpdateRequest meUpdateRequest) {
        AuthUtils.authorizeUserSession(userSession);
        userService.updateMe(userSession.getId(), meUpdateRequest);
        // 프로필 수정
    }

    private String getImageUrl(User user) {
        String imageUrl = "default_profile";

        if (Objects.equals(user.getClass(), Member.class)) {
            Member member = (Member) user;

            // TODO: ProfileImageFile에서 url extract 하기
//            if (member.getImageUrl() != null) {
//                imageUrl = member.getImageUrl();
//            }
        }

        return imageUrl;
    }
}
