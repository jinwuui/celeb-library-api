package com.eunbinlib.api.application.dto.request;

import com.eunbinlib.api.application.domain.comment.Comment;
import com.eunbinlib.api.application.domain.post.Post;
import com.eunbinlib.api.application.domain.user.Member;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "댓글을 작성할 글을 지정해주세요.")
    private Long postId;

    private Long parentId;

    public Comment toEntity(final Member member, final Post post, final Comment parent) {
        return Comment.builder()
                .content(this.content)
                .member(member)
                .post(post)
                .parent(parent)
                .build();
    }
}
