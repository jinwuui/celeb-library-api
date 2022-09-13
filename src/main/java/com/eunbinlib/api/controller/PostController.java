package com.eunbinlib.api.controller;

import com.eunbinlib.api.domain.entity.Post;
import com.eunbinlib.api.domain.request.PostSearch;
import com.eunbinlib.api.domain.request.PostWrite;
import com.eunbinlib.api.domain.request.PostEdit;
import com.eunbinlib.api.domain.response.PostResponse;
import com.eunbinlib.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public Map<String, Long> write(@RequestBody @Valid PostWrite postWrite) {
        Long postId = postService.write(postWrite);
        return Map.of("postId", postId);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse read(@PathVariable Long postId) {
        return postService.read(postId);
    }


    @GetMapping("/posts")
    public List<PostResponse> readMany(@ModelAttribute PostSearch postSearch) {
        return postService.readMany(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody @Valid PostEdit postEdit) {
        postService.edit(postId, postEdit);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }

}
