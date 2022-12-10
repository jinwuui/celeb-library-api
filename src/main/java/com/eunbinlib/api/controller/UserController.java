package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.MemberSession;
import com.eunbinlib.api.dto.request.GuestCreateRequest;
import com.eunbinlib.api.dto.request.MeUpdateRequest;
import com.eunbinlib.api.dto.request.MemberCreateRequest;
import com.eunbinlib.api.dto.response.UserMeResponse;
import com.eunbinlib.api.service.BlockService;
import com.eunbinlib.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    public static final String JOIN_MEMBER_URL = "/api/users/members";
    public static final String JOIN_GUEST_URL ="/api/users/guests";

    private final UserService userService;
    private final BlockService blockService;

    @PostMapping("/members")
    public void createMember(@RequestBody @Valid MemberCreateRequest memberCreateRequest) {
        userService.createMember(memberCreateRequest);
    }

    @PostMapping("/guests")
    public void createGuest(@RequestBody @Valid GuestCreateRequest guestCreateRequest) {
        userService.createGuest(guestCreateRequest);
    }

    @GetMapping("/me")
    public UserMeResponse readMe(MemberSession memberSession) {
        return userService.readMeByUsername(memberSession.getUsername());
    }

    @PatchMapping("/me")
    public void updateMe(MemberSession memberSession, @ModelAttribute @Valid MeUpdateRequest meUpdateRequest) {
        userService.updateMe(memberSession.getId(), meUpdateRequest);
    }

    @PostMapping("/{userId}/block")
    public void blockUser(MemberSession memberSession, @PathVariable Long userId) {
        blockService.blockUser(memberSession.getId(), userId);
    }

    @PostMapping("/{userId}/unblock")
    public void unblockUser(MemberSession memberSession, @PathVariable Long userId) {
        blockService.unblockUser(memberSession.getId(), userId);
    }
}
