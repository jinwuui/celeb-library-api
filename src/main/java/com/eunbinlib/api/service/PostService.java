package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.entity.imagefile.PostImageFile;
import com.eunbinlib.api.domain.entity.post.Post;
import com.eunbinlib.api.domain.entity.post.PostState;
import com.eunbinlib.api.domain.request.PostEdit;
import com.eunbinlib.api.domain.request.PostSearch;
import com.eunbinlib.api.domain.request.PostWrite;
import com.eunbinlib.api.domain.response.*;
import com.eunbinlib.api.exception.type.PostNotFoundException;
import com.eunbinlib.api.repository.post.PostRepository;
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

    @Value("${images.post.dir}")
    private String imagesPostDir;

    public OnlyId write(PostWrite postWrite) {
        try {
            Post post = Post.builder()
                    .title(postWrite.getTitle())
                    .content(postWrite.getContent())
                    .state(PostState.NORMAL)
                    .build();

            List<MultipartFile> images = postWrite.getImages();
            List<PostImageFile> storeFileResult = storeFiles(images);

            for (PostImageFile image : storeFileResult) {
                post.addImage(image);
            }

            Long postId = postRepository.save(post).getId();

            return OnlyId.builder()
                    .id(postId)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PostDetailRes read(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return PostDetailRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public PaginationRes<PostRes> readMany(PostSearch postSearch) {
        List<PostRes> data = postRepository.getList(postSearch).stream()
                .map(PostRes::new)
                .collect(Collectors.toList());

        PaginationMeta meta = PaginationMeta.builder()
                .size(data.size())
                .hasMore(isHasMore(data))
                .build();

        return PaginationRes.<PostRes>builder()
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

    private boolean isHasMore(List<PostRes> data) {
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
