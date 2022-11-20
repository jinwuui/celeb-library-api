package com.eunbinlib.api.dto.request;

import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.post.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentCreateRequest {

    @NotBlank(message = "내용을 입력해주세요.")
    private final String content;

    private final Long postId;

    private final Long mentionId;

    @Builder
    public CommentCreateRequest(String content, Long postId, Long mentionId) {
        this.content = content;
        this.postId = postId;
        this.mentionId = mentionId;
    }

    public Comment toEntity(final Long writerId, final Post post) {
        Comment comment = Comment.builder()
                .writerId(writerId)
                .content(this.content)
                .mentionId(this.mentionId)
                .build();
        comment.setPost(post);

        return comment;
    }
}
