package com.eunbinlib.api.domain.comment;

import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommentTest {

    String content = "댓글 내용";

    @Test
    @DisplayName("댓글의 게시글을 설정하는 경우")
    void setPostSuccess() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        Comment comment = Comment.builder()
                .content(content)
                .build();

        // when
        comment.setPost(post);

        // then
        comment.setPost(post);

        assertThat(comment.getPost())
                .isEqualTo(post);
    }

    @Test
    @DisplayName("댓글의 게시글을 null로 설정하는 경우")
    void setNullPost() {
        // given
        Post post = null;
        Comment comment = Comment.builder()
                .content(content)
                .build();

        // expected
        assertThatThrownBy(() -> comment.setPost(post))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("댓글 내용을 수정하는 경우")
    void updateContent() {
        // given
        Comment comment = Comment.builder()
                .content(content)
                .build();

        // when
        String newContent = "수정된 댓글 내용";
        comment.update(newContent);

        // then
        assertThat(comment.getContent())
                .isEqualTo(newContent);
    }

    @Test
    @DisplayName("댓글 내용을 null 값으로 수정하는 경우")
    void updateNullContent() {
        // given
        Comment comment = Comment.builder()
                .content(content)
                .build();

        // when
        String newContent = null;
        comment.update(newContent);

        // then
        assertThat(comment.getContent())
                .isEqualTo(content);
    }
}
