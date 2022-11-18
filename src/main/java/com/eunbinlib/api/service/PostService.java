package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.entity.imagefile.PostImageFile;
import com.eunbinlib.api.domain.entity.post.Post;
import com.eunbinlib.api.domain.request.PostEdit;
import com.eunbinlib.api.domain.request.PostSearch;
import com.eunbinlib.api.domain.request.PostWrite;
import com.eunbinlib.api.domain.response.*;
import com.eunbinlib.api.exception.type.PostNotFoundException;
import com.eunbinlib.api.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class PostService {

    @Value("${file.dir}")
    private String fileDir;

    private final PostRepository postRepository;

    public OnlyId write(PostWrite postWrite) {

        try {
            List<MultipartFile> images = postWrite.getImages();
            List<PostImageFile> storeFileResult = storeFiles(images);

            Post post = Post.builder()
                    .title(postWrite.getTitle())
                    .content(postWrite.getContent())
                    .images(storeFileResult)
                    .build();

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
        if (file.isEmpty()) {
            return null;
        }

        String fullPath = fileDir + file.getOriginalFilename();

        file.transferTo(new File(fullPath));

        String savedFileName = UUID.randomUUID().toString();

        return PostImageFile.builder()
                .savedFileName(savedFileName)
                .originalFileName(file.getOriginalFilename())
                .extension(file.getContentType())
                .byteSize(file.getSize())
                .build();
    }

    private List<PostImageFile> storeFiles(List<MultipartFile> files) throws IOException {
        if (files.isEmpty()) {
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
}
