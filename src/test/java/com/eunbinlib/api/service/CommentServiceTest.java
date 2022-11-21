package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.repository.comment.CommentRepository;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.CommentCreateRequest;
import com.eunbinlib.api.dto.request.CommentUpdateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import com.eunbinlib.api.exception.type.notfound.CommentNotFoundException;
import com.eunbinlib.api.exception.type.notfound.PostNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.TransactionSystemException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    Comment mockComment;

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


        Comment comment = Comment.builder()
                .content("댓글 내용")
                .member(mockMember)
                .post(mockPost)
                .build();

        mockComment = commentRepository.save(comment);
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("create")
    class Create {

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
        @DisplayName("대댓글을 작성하는 경우")
        void createCommentWithMention() {
            // given
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .content("댓글내용")
                    .postId(mockPost.getId())
                    .parentId(mockComment.getId())
                    .build();

            // when
            OnlyIdResponse onlyIdResponse = commentService.create(mockMember.getId(), request);

            // then
            Comment findComment = commentRepository.findById(onlyIdResponse.getId())
                    .orElseThrow(IllegalArgumentException::new);

            assertThat(onlyIdResponse.getId())
                    .isEqualTo(findComment.getId());
            assertThat(findComment.getParent().getId())
                    .isEqualTo(mockComment.getId());
        }

        @Test
        @DisplayName("게시글을 지정하지 않고 댓글을 작성하는 경우")
        void createCommentNoPostId() {
            // given
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .content("댓글내용")
                    .build();

            // expected
            assertThatThrownBy(() -> commentService.create(mockMember.getId(), request))
                    .isInstanceOf(InvalidDataAccessApiUsageException.class);
        }

        @Test
        @DisplayName("존재하지 않는 게시글에 댓글을 작성하는 경우")
        void createCommentNotExistPost() {
            // given
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .content("댓글내용")
                    .postId(mockPost.getId() + 100L)
                    .build();

            // expected
            assertThatThrownBy(() -> commentService.create(mockMember.getId(), request))
                    .isInstanceOf(PostNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("댓글을 수정하는 경우")
        void updateComment() {
            // given
            CommentUpdateRequest request = CommentUpdateRequest.builder()
                    .content("수정된 댓글내용")
                    .build();

            // when
            commentService.update(mockMember.getId(), mockComment.getId(), request);

            // then
            Comment findComment = commentRepository.findById(mockComment.getId())
                    .orElseThrow(CommentNotFoundException::new);

            assertThat(findComment.getContent())
                    .isEqualTo(request.getContent());
        }

        @Test
        @DisplayName("다른 사람의 댓글을 수정하는 경우")
        void updateCommentOfAnotherUser() {
            // given
            CommentUpdateRequest request = CommentUpdateRequest.builder()
                    .content("수정된 댓글내용")
                    .build();

            Long anotherMemberId = 100L;

            // expected
            assertThatThrownBy(() ->
                    commentService.update(anotherMemberId, mockComment.getId(), request))
                    .isInstanceOf(UnauthorizedException.class);
        }

        @Test
        @DisplayName("존재하지 않는 댓글을 수정하려는 경우")
        void updateCommentNotExist() {
            // given
            CommentUpdateRequest request = CommentUpdateRequest.builder()
                    .content("수정된 댓글내용")
                    .build();

            // expected
            assertThatThrownBy(() ->
                    commentService.update(mockMember.getId(), mockComment.getId() + 100L, request))
                    .isInstanceOf(CommentNotFoundException.class);
        }

        @Test
        @DisplayName("댓글을 빈값으로 수정하는 경우")
        void updateCommentEmptyContent() {
            // given
            CommentUpdateRequest request = CommentUpdateRequest.builder()
                    .content(" ")
                    .build();
            // expected
            assertThatThrownBy(() ->
                    commentService.update(mockMember.getId(), mockComment.getId(), request))
                    .isInstanceOf(TransactionSystemException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("댓글을 삭제하는 경우")
        void deleteComment() {
            // when
            commentService.delete(mockMember.getId(), mockComment.getId());

            // expected
            assertThat(commentRepository.findById(mockComment.getId()).isEmpty())
                    .isTrue();
        }

        @Test
        @DisplayName("다른 사람의 댓글을 삭제하는 경우")
        void deleteCommentOfAnotherUser() {
            Long anotherMemberId = 100L;

            // expected
            assertThatThrownBy(() ->
                    commentService.delete(anotherMemberId, mockComment.getId()))
                    .isInstanceOf(UnauthorizedException.class);
        }

        @Test
        @DisplayName("존재하지 않는 댓글을 삭제하려는 경우")
        void deleteCommentNotExist() {
            // expected
            assertThatThrownBy(() ->
                    commentService.delete(mockMember.getId(), mockComment.getId() + 100L))
                    .isInstanceOf(CommentNotFoundException.class);
        }
    }
}