package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.repository.comment.CommentRepository;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.CommentCreateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    Member mockMember;

    Post mockPost;

    @BeforeEach
    void beforeEach() {
        mockMember = userRepository.save(Member.builder()
                .username("mockMember")
                .nickname("mockMember")
                .password("mockPassword")
                .build()
        );

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);

        mockPost = postRepository.save(post);
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 작성")
    void createComment() {
        // given
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("댓글내용")
                .postId(mockPost.getId())
                .build();

        // when
        OnlyIdResponse onlyIdResponse = commentService.create(mockMember.getId(), request);

        // then
        Comment findComment = commentRepository.findById(onlyIdResponse.getId())
                .orElseThrow(IllegalArgumentException::new);
        assertThat(onlyIdResponse.getId())
                .isEqualTo(findComment.getId());
    }

    @Test
    @DisplayName("유저 멘션이 있는 댓글 작성(대댓글)")
    void createCommentWithMention() {
        // given
        Member mockMember2 = userRepository.save(Member.builder()
                .username("mockMember2")
                .nickname("mockMember2")
                .password("mockPassword2")
                .build()
        );

        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("댓글내용")
                .postId(mockPost.getId())
                .mentionId(mockMember2.getId())
                .build();

        // when
        OnlyIdResponse onlyIdResponse = commentService.create(mockMember.getId(), request);

        // then
        Comment findComment = commentRepository.findById(onlyIdResponse.getId())
                .orElseThrow(IllegalArgumentException::new);
        assertThat(onlyIdResponse.getId())
                .isEqualTo(findComment.getId());
    }
}