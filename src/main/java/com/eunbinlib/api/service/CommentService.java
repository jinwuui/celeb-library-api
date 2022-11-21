package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.repository.comment.CommentRepository;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.user.MemberRepository;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.CommentCreateRequest;
import com.eunbinlib.api.dto.request.CommentUpdateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.exception.type.notfound.CommentNotFoundException;
import com.eunbinlib.api.exception.type.notfound.PostNotFoundException;
import com.eunbinlib.api.exception.type.notfound.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final EntityManager em;
    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public OnlyIdResponse create(Long userId, CommentCreateRequest commentCreateRequest) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(commentCreateRequest.getPostId())
                .orElseThrow(PostNotFoundException::new);
        Comment parent = null;
        if (commentCreateRequest.getParentId() != null) {
            parent = commentRepository.findById(commentCreateRequest.getParentId())
                    .orElseThrow(CommentNotFoundException::new);
        }


        Comment comment = commentCreateRequest.toEntity(member, post, parent);

        commentRepository.save(comment);


        return OnlyIdResponse.builder()
                .id(comment.getId())
                .build();
    }

    @Transactional
    public void update(Long userId, Long commentId, CommentUpdateRequest commentUpdateRequest) {

    }

    @Transactional
    public void delete(Long userId, Long commentId) {


    }
}
