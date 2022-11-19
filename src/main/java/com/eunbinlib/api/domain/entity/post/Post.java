package com.eunbinlib.api.domain.entity.post;

import com.eunbinlib.api.domain.entity.BaseTimeEntity;
import com.eunbinlib.api.domain.entity.imagefile.PostImageFile;
import com.eunbinlib.api.domain.request.PostEdit;
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

    @Builder
    public Post(String title, PostState state, String content, Long likeCount, Long viewCount, List<PostImageFile> images) {
        this.title = title;
        this.state = state;
        this.content = content;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.images = images;
    }

    public void edit(PostEdit postEdit) {
        PostEdit fixedPostEdit = PostEdit.builder()
                .title(postEdit.getTitle() != null ? postEdit.getTitle() : title)
                .content(postEdit.getContent() != null ? postEdit.getContent() : content)
                .build();

        title = fixedPostEdit.getTitle();
        content = fixedPostEdit.getContent();
    }

    public void addImage(PostImageFile image) {
        if (this.images == null) this.images = new ArrayList<>();

        this.images.add(image);
        if (image.getPost() != this) {
            image.setPost(this);
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
