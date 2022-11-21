package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.imagefile.PostImageFile;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.user.MemberRepository;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.dto.response.*;
import com.eunbinlib.api.exception.type.notfound.PostNotFoundException;
import com.eunbinlib.api.exception.type.notfound.UserNotFoundException;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Value("${images.post.dir}")
    private String imagesPostDir;

    @Transactional
    public OnlyIdResponse create(Long userId, PostCreateRequest postCreateRequest) {

        Post post = Post.builder()
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .state(PostState.NORMAL)
                .build();

        Member writer = memberRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        post.setMember(writer);

        List<MultipartFile> images = postCreateRequest.getImages();
        List<PostImageFile> storeFileResult = null;

        try {
            storeFileResult = storeFiles(images);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (PostImageFile image : storeFileResult) {
            post.addImage(image);
        }

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
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (!post.getMember().getId().equals(userId)) {
            throw new UnauthorizedException();
        }

        post.update(postUpdateRequest.getTitle(), postUpdateRequest.getContent());
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (!post.getMember().getId().equals(userId)) {
            throw new UnauthorizedException();
        }

        post.delete();
    }

    private boolean isHasMore(List<PostResponse> data) {
        return !data.isEmpty() && postRepository.existsNext(data.get(data.size() - 1).getId());
    }

    private PostImageFile storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (!(file.getContentType().contains("image") || file.getContentType().contains("video"))) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String storeFilename = createStoreFilename(originalFilename);

        file.transferTo(new File(getPullPath(storeFilename)));

        return PostImageFile.builder()
                .savedFilename(storeFilename)
                .originalFilename(originalFilename)
                .contentType(file.getContentType())
                .byteSize(file.getSize())
                .build();
    }

    private List<PostImageFile> storeFiles(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<PostImageFile> result = new ArrayList<>();

        for (MultipartFile file : files) {
            PostImageFile storedFile = storeFile(file);

            if (storedFile != null) {
                result.add(storedFile);
            }
        }

        return result;
    }

    private String getPullPath(String filename) {
        return imagesPostDir + filename;
    }

    private String createStoreFilename(String originalFilename) {
        String extension = extractExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }

    private String extractExtension(String originalFilename) {
        int index = originalFilename.lastIndexOf(".");
        return originalFilename.substring(index + 1);
    }
}
