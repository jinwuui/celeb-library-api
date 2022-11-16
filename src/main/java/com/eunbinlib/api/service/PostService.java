package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.entity.post.Post;
import com.eunbinlib.api.domain.request.PostEdit;
import com.eunbinlib.api.domain.request.PostSearch;
import com.eunbinlib.api.domain.request.PostWrite;
import com.eunbinlib.api.domain.response.*;
import com.eunbinlib.api.exception.type.PostNotFoundException;
import com.eunbinlib.api.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public OnlyId write(PostWrite postWrite) {
        Post post = Post.builder()
                .title(postWrite.getTitle())
                .content(postWrite.getContent())
                .build();

        Long postId = postRepository.save(post).getId();

        return OnlyId.builder()
                .id(postId)
                .build();
    }

    public PostDetailResponse read(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public PaginationRes<PostResponse> readMany(PostSearch postSearch) {
        List<PostResponse> data = postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());

        PaginationMeta meta = PaginationMeta.builder()
                .size(data.size())
                .hasMore(isHasMore(data))
                .build();

        return PaginationRes.<PostResponse>builder()
                .meta(meta)
                .data(data)
                .build();
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

    private boolean isHasMore(List<PostResponse> data) {
        return !data.isEmpty() && postRepository.existsNext(data.get(data.size() - 1).getId());
    }
}
