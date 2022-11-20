package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.dto.response.PaginationResponse;
import com.eunbinlib.api.dto.response.PostDetailResposne;
import com.eunbinlib.api.dto.response.PostResponse;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import com.eunbinlib.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public OnlyIdResponse write(UserSession userSession, @ModelAttribute @Valid PostCreateRequest postCreateRequest) {

        String userType = userSession.getUserType();
        if (StringUtils.equals(userType, "guest")) {
            throw new UnauthorizedException();
        }

        return postService.write(userSession.getId(), postCreateRequest);
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
    public void edit(@PathVariable Long postId, @RequestBody @Valid PostUpdateRequest postUpdateRequest) {
        postService.edit(postId, postUpdateRequest);
    }

    @DeleteMapping("/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }

}
