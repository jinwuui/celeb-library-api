package com.eunbinlib.api.domain.post;

import com.eunbinlib.api.domain.BaseTimeEntity;
import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.imagefile.PostImageFile;
import com.eunbinlib.api.domain.user.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.util.CollectionUtils;

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
    public Post(final String title, final String content, final Long likeCount, final Long viewCount, final Member member) {
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.member = member;
        this.state = PostState.NORMAL;
    }

    public void updateTitleAndContent(final String title, final String content) {
        this.title = title == null ? this.title : title;
        this.content = content == null ? this.content : content;
    }

    public void updateImages(final List<Long> deleteIdList, final List<BaseImageFile> newImages) {
        deleteImagesById(deleteIdList);
        addImages(newImages);
    }

    public void addImage(final BaseImageFile baseImageFile) {
        if (baseImageFile == null) {
            return;
        }

        PostImageFile postImageFile = PostImageFile.builder()
                .baseImageFile(baseImageFile)
                .post(this)
                .build();

        this.images.add(postImageFile);
    }

    public void addImages(final List<BaseImageFile> baseImageFiles) {
        for (BaseImageFile baseImageFile : baseImageFiles) {
            addImage(baseImageFile);
        }
    }

    public void deleteImagesById(final List<Long> deleteIdList) {
        if (CollectionUtils.isEmpty(deleteIdList)) {
            return;
        }

        for (PostImageFile image : this.images) {
            if (deleteIdList.contains(image.getId())) {
                this.images.remove(image);
                image.setPost(null);
            }
        }
    }

    public void addComment(final Comment comment) {
        this.comments.add(comment);
        if (comment.getPost() != this) {
            comment.setPost(this);
        }
    }

    public void setMember(final Member member) {
        this.member = member;
        if (!member.getPosts().contains(this)) {
            member.getPosts().add(this);
        }
    }

    public void delete() {
        this.state = PostState.DELETED;
    }

    public void increaseViewCount() {
        ++viewCount;
    }

    public void increaseLikeCount() {
        ++likeCount;
    }

    public void decreaseLikeCount() {
        if (likeCount > 0) {
            --likeCount;
        }
    }
}
