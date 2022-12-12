package com.eunbinlib.api.application.service;

import com.eunbinlib.api.application.domain.comment.Comment;
import com.eunbinlib.api.application.domain.post.Post;
import com.eunbinlib.api.application.domain.repository.comment.CommentRepository;
import com.eunbinlib.api.application.domain.user.Member;
import com.eunbinlib.api.application.dto.request.CommentCreateRequest;
import com.eunbinlib.api.application.dto.request.CommentUpdateRequest;
import com.eunbinlib.api.application.dto.response.OnlyIdResponse;
import com.eunbinlib.api.application.exception.type.ForbiddenAccessException;
import com.eunbinlib.api.application.exception.type.notfound.CommentNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostService postService;

    private final UserService userService;

    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
    }

    @Transactional
    public OnlyIdResponse create(Long userId, CommentCreateRequest commentCreateRequest) {
        Member member = userService.findMemberById(userId);
        Post post = postService.findById(commentCreateRequest.getPostId());

        Comment parent = null;
        if (commentCreateRequest.getParentId() != null) {
            parent = findById(commentCreateRequest.getParentId());
        }

        Comment comment = commentCreateRequest.toEntity(member, post, parent);

        commentRepository.save(comment);

        return OnlyIdResponse.builder()
                .id(comment.getId())
                .build();
    }

    @Transactional
    public void update(Long userId, Long commentId, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = findById(commentId);

        validateWriter(userId, comment.getMember().getId());

        comment.update(commentUpdateRequest.getContent());
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = findById(commentId);

        validateWriter(userId, comment.getMember().getId());

        comment.delete();
    }

    private void validateWriter(Long userId, Long commentWriterId) {
        if (!commentWriterId.equals(userId)) {
            throw new ForbiddenAccessException();
        }
    }
}
