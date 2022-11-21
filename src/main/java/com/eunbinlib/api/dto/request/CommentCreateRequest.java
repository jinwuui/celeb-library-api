package com.eunbinlib.api.dto.request;

import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.user.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CommentCreateRequest {

    @NotBlank(message = "내용을 입력해주세요.")
    private final String content;

    @NotNull(message = "댓글을 작성할 글을 지정해주세요.")
    private final Long postId;

    private final Long parentId;

    @Builder
    public CommentCreateRequest(String content, Long postId, Long parentId) {
        this.content = content;
        this.postId = postId;
        this.parentId = parentId;
    }

    public Comment toEntity(final Member member, final Post post, final Comment parent) {
        return Comment.builder()
                .content(this.content)
                .member(member)
                .post(post)
                .parent(parent)
                .build();
    }
}
