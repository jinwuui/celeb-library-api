package com.eunbinlib.api.domain.comment;

import com.eunbinlib.api.domain.BaseTimeEntity;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.user.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "댓글을 입력해주세요.")
    String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CommentState state;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;

    @OneToOne
    @JoinColumn(name = "PARENT_ID", nullable = true)
    private Comment parent;

    @Builder
    public Comment(final String content, final Member member, final Post post, final Comment parent) {
        this.content = content;
        this.member = member;
        this.post = post;
        this.parent = parent;
        this.state = CommentState.NORMAL;
    }

    public void setPost(final Post post) {
        if (post == null) {
            throw new IllegalArgumentException("댓글이 속하는 게시글은 null 값이 될 수 없습니다.");
        }

        this.post = post;
    }

    public void update(final String content) {
        this.content = content != null ? content : this.content;
    }

    public void delete() {
        this.state = CommentState.DELETED;
    }
}
