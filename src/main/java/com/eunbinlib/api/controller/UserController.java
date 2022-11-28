package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.dto.request.GuestCreateRequest;
import com.eunbinlib.api.dto.request.MeUpdateRequest;
import com.eunbinlib.api.dto.request.MemberCreateRequest;
import com.eunbinlib.api.dto.response.UserMeResponse;
import com.eunbinlib.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.eunbinlib.api.auth.utils.AuthUtils.authorizePassOnlyMember;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    public static final String JOIN_MEMBER_URL = "/api/users/members";
    public static final String JOIN_GUEST_URL ="/api/users/guests";

    private final UserService userService;

    @PostMapping("/members")
    public void createMember(@RequestBody @Valid MemberCreateRequest memberCreateRequest) {
        userService.createMember(memberCreateRequest);
    }

    @PostMapping("/guests")
    public void createGuest(@RequestBody @Valid GuestCreateRequest guestCreateRequest) {
        userService.createGuest(guestCreateRequest);
    }

    @GetMapping("/me")
    public UserMeResponse readMe(UserSession userSession) {
        authorizePassOnlyMember(userSession);

        return userService.readMeByUsername(userSession.getUsername());
    }

    @PatchMapping("/me")
    public void updateMe(UserSession userSession, @ModelAttribute @Valid MeUpdateRequest meUpdateRequest) {
        authorizePassOnlyMember(userSession);

        userService.updateMe(userSession.getId(), meUpdateRequest);
    }
}
