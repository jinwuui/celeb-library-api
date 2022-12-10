package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.MemberSession;
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

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OnlyIdResponse create(MemberSession memberSession, @RequestBody @Valid CommentCreateRequest commentCreateRequest) {
        return commentService.create(memberSession.getId(), commentCreateRequest);
    }

    @PatchMapping("/{commentId}")
    public void update(MemberSession memberSession, @PathVariable Long commentId, @RequestBody @NotBlank CommentUpdateRequest commentUpdateRequest) {
        commentService.update(memberSession.getId(), commentId, commentUpdateRequest);
    }

    @DeleteMapping("/{commentId}")
    public void delete(MemberSession memberSession, @PathVariable Long commentId) {
        commentService.delete(memberSession.getId(), commentId);
    }
}
