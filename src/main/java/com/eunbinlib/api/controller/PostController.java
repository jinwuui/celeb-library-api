package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.MemberSession;
import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.dto.response.PaginationResponse;
import com.eunbinlib.api.dto.response.PostResponse;
import com.eunbinlib.api.dto.response.postdetailresponse.PostDetailResponse;
import com.eunbinlib.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OnlyIdResponse create(MemberSession memberSession, @ModelAttribute @Valid PostCreateRequest postCreateRequest) {
        return postService.create(memberSession.getId(), postCreateRequest);
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
    public void update(MemberSession memberSession, @PathVariable Long postId, @ModelAttribute @Valid PostUpdateRequest postUpdateRequest) {
        postService.update(memberSession.getId(), postId, postUpdateRequest);
    }

    @DeleteMapping("/{postId}")
    public void delete(MemberSession memberSession, @PathVariable Long postId) {
        postService.delete(memberSession.getId(), postId);
    }

    @PostMapping("/{postId}/like")
    public void likePost(MemberSession memberSession, @PathVariable Long postId, @RequestParam Boolean isLike) {
        postService.likePost(memberSession.getId(), postId, isLike);
    }
}
