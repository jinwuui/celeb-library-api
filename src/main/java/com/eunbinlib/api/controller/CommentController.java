package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.dto.request.CommentCreateRequest;
import com.eunbinlib.api.dto.request.CommentUpdateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.eunbinlib.api.auth.utils.AuthService.authorizePassOnlyMember;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OnlyIdResponse create(UserSession userSession, @RequestBody @Valid CommentCreateRequest commentCreateRequest) {
        authorizePassOnlyMember(userSession);
        return commentService.create(userSession.getId(), commentCreateRequest);
    }

    @PatchMapping("/{commentId}")
    public void update(UserSession userSession, @PathVariable Long commentId, @RequestBody @NotBlank CommentUpdateRequest commentUpdateRequest) {
        authorizePassOnlyMember(userSession);
        commentService.update(userSession.getId(), commentId, commentUpdateRequest);
    }

    @DeleteMapping("/{commentId}")
    public void delete(UserSession userSession, @PathVariable Long commentId) {
        authorizePassOnlyMember(userSession);
        commentService.delete(userSession.getId(), commentId);
    }
}
