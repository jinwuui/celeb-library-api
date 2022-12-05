package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.comment.CommentState;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.CommentCreateRequest;
import com.eunbinlib.api.dto.request.CommentUpdateRequest;
import com.eunbinlib.api.dto.response.OnlyIdResponse;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import com.eunbinlib.api.exception.type.notfound.CommentNotFoundException;
import com.eunbinlib.api.exception.type.notfound.PostNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.TransactionSystemException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class CommentServiceTest extends ServiceTest {

    @Autowired
    CommentService commentService;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("댓글 작성")
        void createComment() {
            // given
            Member member = getMember();
            Post post = getPost(member);

            CommentCreateRequest request =  new CommentCreateRequest("댓글 내용", post.getId(), null);

            // when
            OnlyIdResponse onlyIdResponse = commentService.create(member.getId(), request);

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
            Member member = getMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            CommentCreateRequest request =  new CommentCreateRequest("댓글 내용", post.getId(), comment.getId());

            // when
            OnlyIdResponse onlyIdResponse = commentService.create(member.getId(), request);

            // then
            Comment findComment = commentRepository.findById(onlyIdResponse.getId())
                    .orElseThrow(IllegalArgumentException::new);

            assertThat(onlyIdResponse.getId())
                    .isEqualTo(findComment.getId());
            assertThat(findComment.getParent().getId())
                    .isEqualTo(comment.getId());
        }

        @Test
        @DisplayName("게시글을 지정하지 않고 댓글을 작성하는 경우")
        void createCommentNoPostId() {
            // given
            Member member = getMember();
            CommentCreateRequest request =  new CommentCreateRequest("댓글 내용", null, null);

            // expected
            assertThatThrownBy(() -> commentService.create(member.getId(), request))
                    .isInstanceOf(InvalidDataAccessApiUsageException.class);
        }

        @Test
        @DisplayName("존재하지 않는 게시글에 댓글을 작성하는 경우")
        void createCommentNotExistPost() {
            // given
            Member member = getMember();
            Post post = getPost(member);

            CommentCreateRequest request =  new CommentCreateRequest("댓글 내용", post.getId() + 100L, null);

            // expected
            assertThatThrownBy(() -> commentService.create(member.getId(), request))
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
            Member member = getMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용");

            // when
            commentService.update(member.getId(), comment.getId(), request);

            // then
            Comment findComment = commentRepository.findById(comment.getId())
                    .orElseThrow(CommentNotFoundException::new);

            assertThat(findComment.getContent())
                    .isEqualTo(request.getContent());
        }

        @Test
        @DisplayName("다른 사람의 댓글을 수정하는 경우")
        void updateCommentOfAnotherUser() {
            // given
            Member member = getMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용");

            Long anotherMemberId = 100L;

            // expected
            assertThatThrownBy(() ->
                    commentService.update(anotherMemberId, comment.getId(), request))
                    .isInstanceOf(UnauthorizedException.class);
        }

        @Test
        @DisplayName("존재하지 않는 댓글을 수정하려는 경우")
        void updateCommentNotExist() {
            // given
            Member member = getMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용");

            // expected
            assertThatThrownBy(() ->
                    commentService.update(member.getId(), comment.getId() + 100L, request))
                    .isInstanceOf(CommentNotFoundException.class);
        }

        @Test
        @DisplayName("댓글을 빈값으로 수정하는 경우")
        void updateCommentEmptyContent() {
            // given
            Member member = getMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            CommentUpdateRequest request = new CommentUpdateRequest("  ");

            // expected
            assertThatThrownBy(() ->
                    commentService.update(member.getId(), comment.getId(), request))
                    .isInstanceOf(TransactionSystemException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("댓글을 삭제하는 경우")
        void deleteComment() {
            // given
            Member member = getMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            // when
            commentService.delete(member.getId(), comment.getId());

            // expected
            Comment findComment = commentRepository.findById(comment.getId())
                    .orElseThrow(IllegalArgumentException::new);
            assertThat(findComment.getState())
                    .isEqualTo(CommentState.DELETED);
        }

        @Test
        @DisplayName("다른 사람의 댓글을 삭제하는 경우")
        void deleteCommentOfAnotherUser() {
            // given
            Long anotherMemberId = 100L;
            Member member = getMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            // expected
            assertThatThrownBy(() ->
                    commentService.delete(anotherMemberId, comment.getId()))
                    .isInstanceOf(UnauthorizedException.class);
        }

        @Test
        @DisplayName("존재하지 않는 댓글을 삭제하려는 경우")
        void deleteCommentNotExist() {
            // given
            Member member = getMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            // expected
            assertThatThrownBy(() ->
                    commentService.delete(member.getId(), comment.getId() + 100L))
                    .isInstanceOf(CommentNotFoundException.class);
        }
    }
}