package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.postlike.PostLike;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.domain.repository.postlike.PostLikeRepository;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.dto.response.PaginationMeta;
import com.eunbinlib.api.dto.response.PaginationResponse;
import com.eunbinlib.api.dto.response.PostResponse;
import com.eunbinlib.api.dto.response.postdetailresponse.PostDetailResponse;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import com.eunbinlib.api.exception.type.notfound.PostNotFoundException;
import com.eunbinlib.api.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostImageFileRepository postImageFileRepository;

    private final PostLikeRepository postLikeRepository;

    private final UserService userService;

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    @Transactional
    public OnlyIdResponse create(Long userId, PostCreateRequest postCreateRequest) {
        Member writer = userService.findMemberById(userId);

        Post post = postCreateRequest.toEntity(writer);

        List<BaseImageFile> baseImageFiles = ImageUtils.storeImages(postCreateRequest.getImages());

        post.addImages(baseImageFiles);

        Long postId = postRepository.save(post).getId();

        return OnlyIdResponse.from(postId);
    }

    @Transactional
    public PostDetailResponse readDetail(Long postId) {
        Post post = postRepository.findByIdAndState(postId, PostState.NORMAL)
                .orElseThrow(PostNotFoundException::new);

        postRepository.findWithImagesByIdAndState(postId, PostState.NORMAL);

        post.increaseViewCount();

        return PostDetailResponse.from(post);
    }

    public PaginationResponse<PostResponse> readMany(Long userId, PostReadRequest postReadRequest) {
        List<Post> findPosts = findPost(userId, postReadRequest.getLimit(), postReadRequest.getAfter());

        List<PostResponse> data = findPosts.stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());

        PaginationMeta meta = PaginationMeta.builder()
                .size(data.size())
                .hasMore(isHasMore(userId, data))
                .build();

        return PaginationResponse.<PostResponse>builder()
                .meta(meta)
                .data(data)
                .build();
    }

    @Transactional
    public void update(Long userId, Long postId, PostUpdateRequest postUpdateRequest) {
        Post post = postRepository.findWithImagesByIdAndState(postId, PostState.NORMAL)
                .orElseThrow(PostNotFoundException::new);

        validateWriter(userId, post.getMember().getId());

        post.updateTitleAndContent(postUpdateRequest.getTitle(), postUpdateRequest.getContent());

        List<BaseImageFile> newImages = ImageUtils.storeImages(postUpdateRequest.getNewImages());
        post.updateImages(postUpdateRequest.getDeleteIdList(), newImages);
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = findById(postId);

        validateWriter(userId, post.getMember().getId());

        post.delete();
    }

    public void likePost(Long userId, Long postId, Boolean isLike) {
        Optional<PostLike> optionalPostLike = postLikeRepository.findByMemberIdAndPostId(userId, postId);

        boolean alreadyLike = optionalPostLike.isPresent() && isLike;
        boolean alreadyUnlike = optionalPostLike.isEmpty() && !isLike;
        if (alreadyLike || alreadyUnlike) {
            return;
        }

        if (optionalPostLike.isPresent()) {
            postLikeRepository.delete(optionalPostLike.get());
        } else {
            Member member = userService.findMemberById(userId);
            Post post = findById(postId);

            postLikeRepository.save(PostLike.builder()
                    .member(member)
                    .post(post)
                    .build());
        }
    }

    private List<Post> findPost(Long userId, long limit, Long after) {
        if (after == null) {
            return postRepository.findPosts(limit, userId);
        } else {
            return postRepository.findPostsWithAfterCondition(limit, after, userId);
        }
    }

    private boolean isHasMore(Long userId, List<PostResponse> data) {
        return !data.isEmpty() && postRepository.existsNext(userId, data.get(data.size() - 1).getId());
    }

    private void validateWriter(Long userId, Long postWriterId) {
        if (!postWriterId.equals(userId)) {
            throw new UnauthorizedException();
        }
    }
}
