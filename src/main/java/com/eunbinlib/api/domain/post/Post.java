package com.eunbinlib.api.domain.post;

import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.BaseTimeEntity;
import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.imagefile.PostImageFile;
import com.eunbinlib.api.domain.user.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PostState state;

    @Lob
    @NotNull
    private String content;

    @ColumnDefault("0")
    private Long likeCount;

    @ColumnDefault("0")
    private Long viewCount;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<PostImageFile> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Builder
    public Post(String title, PostState state, String content, Long likeCount, Long viewCount, Member member) {
        this.title = title;
        this.state = state;
        this.content = content;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.member = member;
    }

    public void update(final String title, final String content) {
        this.title = title == null ? this.title : title;
        this.content = content == null ? this.content : content;
    }

    public void delete() {
        this.state = PostState.DELETED;
    }

    public void addImage(BaseImageFile baseImageFile) {
        PostImageFile postImageFile = new PostImageFile(baseImageFile, this);

        this.images.add(postImageFile);
        if (postImageFile.getPost() != this) {
            postImageFile.setPost(this);
        }
    }

    public void addImages(List<BaseImageFile> baseImageFiles) {
        for (BaseImageFile baseImageFile : baseImageFiles) {
            addImage(baseImageFile);
        }
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        if (comment.getPost() != this) {
            comment.setPost(this);
        }
    }

    public void setMember(Member member) {
        this.member = member;
        if (!member.getPosts().contains(this)) {
            member.getPosts().add(this);
        }
    }

    public void increaseViewCount() {
        ++viewCount;
    }

    public void increaseLikeCount() {
        ++likeCount;
    }

    public void decreaseLikeCount() {
        --likeCount;
    }
}
