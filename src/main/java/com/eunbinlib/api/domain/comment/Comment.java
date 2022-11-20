package com.eunbinlib.api.domain.comment;

import com.eunbinlib.api.domain.common.BaseTimeEntity;
import com.eunbinlib.api.domain.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long writerId;

    @Lob
    @NotNull
    String content;

    private Long mentionId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;

    @Builder
    public Comment(final Long writerId, final String content, final Long mentionId) {
        this.writerId = writerId;
        this.content = content;
        this.mentionId = mentionId;
    }

    public void setPost(Post post) {
        this.post = post;
        if (!post.getComments().contains(this)) {
            post.getComments().add(this);
        }
    }
}
