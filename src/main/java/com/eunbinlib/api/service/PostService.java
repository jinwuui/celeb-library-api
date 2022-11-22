package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.dto.response.*;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import com.eunbinlib.api.exception.type.notfound.PostNotFoundException;
import com.eunbinlib.api.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final UserService userService;

    @Value("${images.post.dir}")
    private String postImageDir;

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    @Transactional
    public OnlyIdResponse create(Long userId, PostCreateRequest postCreateRequest) {

        Member writer = userService.findMemberById(userId);

        Post post = Post.builder()
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .state(PostState.NORMAL)
                .member(writer)
                .build();

        List<BaseImageFile> baseImageFiles = ImageUtils.storeImages(
                postImageDir, postCreateRequest.getImages());

        post.addImages(baseImageFiles);

        Long postId = postRepository.save(post).getId();

        return OnlyIdResponse.builder()
                .id(postId)
                .build();
    }

    public PostDetailResposne read(Long postId) {

        Post post = postRepository.findByIdAndStateNot(postId, PostState.DELETED)
                .orElseThrow(PostNotFoundException::new);

        return PostDetailResposne.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public PaginationResponse<PostResponse> readMany(PostReadRequest postReadRequest) {
        List<PostResponse> data = postRepository.getList(
                        postReadRequest.getLimit(),
                        postReadRequest.getAfter()
                )
                .stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());

        PaginationMeta meta = PaginationMeta.builder()
                .size(data.size())
                .hasMore(isHasMore(data))
                .build();

        return PaginationResponse.<PostResponse>builder()
                .meta(meta)
                .data(data)
                .build();
    }

    @Transactional
    public void update(Long userId, Long postId, PostUpdateRequest postUpdateRequest) {
        Post post = findById(postId);

        validateWriter(userId, post.getMember().getId());

        post.update(postUpdateRequest.getTitle(), postUpdateRequest.getContent());
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = findById(postId);

        validateWriter(userId, post.getMember().getId());

        post.delete();
    }

    private boolean isHasMore(List<PostResponse> data) {
        return !data.isEmpty() && postRepository.existsNext(data.get(data.size() - 1).getId());
    }

    private void validateWriter(Long userId, Long postWriterId) {
        if (!postWriterId.equals(userId)) {
            throw new UnauthorizedException();
        }
    }
}
