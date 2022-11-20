package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.dto.request.CommentCreateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import com.eunbinlib.api.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OnlyIdResponse create(UserSession userSession, @RequestBody @Valid CommentCreateRequest commentCreateRequest) {

        String userType = userSession.getUserType();
        if (StringUtils.equals(userType, "guest")) {
            throw new UnauthorizedException();
        }

        return commentService.create(userSession.getId(), commentCreateRequest);
    }
}
