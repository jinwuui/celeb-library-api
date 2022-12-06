package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.dto.response.PaginationResponse;
import com.eunbinlib.api.dto.response.postdetailresponse.PostDetailResponse;
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
    public PostDetailResponse readDetail(@PathVariable Long postId) {
        return postService.readDetail(postId);
    }

    @GetMapping()
    public PaginationResponse<PostResponse> readMany(UserSession userSession, @ModelAttribute PostReadRequest postReadRequest) {
        return postService.readMany(userSession.getId(), postReadRequest);
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

    @PostMapping("/{postId}/like")
    public void likePost(UserSession userSession, @PathVariable Long postId, @RequestParam Boolean isLike) {
        authorizePassOnlyMember(userSession);
        postService.likePost(userSession.getId(), postId, isLike);
    }
}
