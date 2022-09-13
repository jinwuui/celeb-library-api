package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.entity.Post;
import com.eunbinlib.api.domain.request.PostEdit;
import com.eunbinlib.api.domain.request.PostSearch;
import com.eunbinlib.api.domain.request.PostWrite;
import com.eunbinlib.api.domain.response.PostResponse;
import com.eunbinlib.api.exception.type.PostNotFoundException;
import com.eunbinlib.api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Long write(PostWrite postWrite) {
        Post post = Post.builder()
                .title(postWrite.getTitle())
                .content(postWrite.getContent())
                .build();

        return postRepository.save(post).getId();
    }

    public PostResponse read(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public List<PostResponse> readMany(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void edit(Long postId, PostEdit postEdit) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        post.edit(postEdit);
    }

    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        postRepository.delete(post);
    }

}
