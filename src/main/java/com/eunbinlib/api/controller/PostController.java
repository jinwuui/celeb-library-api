package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.dto.response.PaginationResponse;
import com.eunbinlib.api.dto.response.PostDetailResposne;
import com.eunbinlib.api.dto.response.PostResponse;
import com.eunbinlib.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.eunbinlib.api.auth.utils.AuthUtils.authorizePassOnlyMember;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OnlyIdResponse create(UserSession userSession, @ModelAttribute @Valid PostCreateRequest postCreateRequest) {
        authorizePassOnlyMember(userSession);
        return postService.create(userSession.getId(), postCreateRequest);
    }

    @GetMapping("/{postId}")
    public PostDetailResposne read(@PathVariable Long postId) {
        return postService.read(postId);
    }

    @GetMapping()
    public PaginationResponse<PostResponse> readMany(@ModelAttribute PostReadRequest postReadRequest) {
        return postService.readMany(postReadRequest);
    }

    @PatchMapping("/{postId}")
    public void update(UserSession userSession, @PathVariable Long postId, @RequestBody @Valid PostUpdateRequest postUpdateRequest) {
        authorizePassOnlyMember(userSession);
        postService.update(userSession.getId(), postId, postUpdateRequest);
    }

    @DeleteMapping("/{postId}")
    public void delete(UserSession userSession, @PathVariable Long postId) {
        authorizePassOnlyMember(userSession);
        postService.delete(userSession.getId(), postId);
    }
}
