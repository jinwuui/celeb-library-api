package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.repository.comment.CommentRepository;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.dto.request.CommentCreateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.exception.type.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    @Transactional
    public OnlyIdResponse create(Long userId, CommentCreateRequest commentCreateRequest) {
        Post findPost = postRepository.findById(commentCreateRequest.getPostId())
                .orElseThrow(PostNotFoundException::new);

        Comment comment = commentCreateRequest.toEntity(userId, findPost);
        commentRepository.save(comment);

        return OnlyIdResponse.builder()
                .id(comment.getId())
                .build();
    }
}
