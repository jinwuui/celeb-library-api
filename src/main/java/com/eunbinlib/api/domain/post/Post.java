package com.eunbinlib.api.domain.post;

import com.eunbinlib.api.domain.common.BaseTimeEntity;
import com.eunbinlib.api.domain.imagefile.PostImageFile;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Builder
    public Post(String title, PostState state, String content, Long likeCount, Long viewCount) {
        this.title = title;
        this.state = state;
        this.content = content;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
    }

    public void edit(PostUpdateRequest postUpdateRequest) {
        PostUpdateRequest fixedPostUpdateRequest = PostUpdateRequest.builder()
                .title(postUpdateRequest.getTitle() != null ? postUpdateRequest.getTitle() : title)
                .content(postUpdateRequest.getContent() != null ? postUpdateRequest.getContent() : content)
                .build();

        title = fixedPostUpdateRequest.getTitle();
        content = fixedPostUpdateRequest.getContent();
    }

    public void addImage(PostImageFile image) {
        this.images.add(image);
        if (image.getPost() != this) {
            image.setPost(this);
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
