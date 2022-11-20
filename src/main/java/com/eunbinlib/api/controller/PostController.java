package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.domain.request.PostEdit;
import com.eunbinlib.api.domain.request.PostSearch;
import com.eunbinlib.api.domain.request.PostWrite;
import com.eunbinlib.api.domain.response.OnlyId;
import com.eunbinlib.api.domain.response.PaginationRes;
import com.eunbinlib.api.domain.response.PostDetailRes;
import com.eunbinlib.api.domain.response.PostRes;
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
    public OnlyId write(UserSession userSession, @ModelAttribute @Valid PostWrite postWrite) {

        String userType = userSession.getUserType();
        if (StringUtils.equals(userType, "guest")) {
            throw new UnauthorizedException();
        }

        return postService.write(userSession.getId(), postWrite);
    }

    @GetMapping("/{postId}")
    public PostDetailRes read(@PathVariable Long postId) {
        return postService.read(postId);
    }

    @GetMapping()
    public PaginationRes<PostRes> readMany(@ModelAttribute PostSearch postSearch) {
        return postService.readMany(postSearch);
    }

    @PatchMapping("/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody @Valid PostEdit postEdit) {
        postService.edit(postId, postEdit);
    }

    @DeleteMapping("/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }

}
