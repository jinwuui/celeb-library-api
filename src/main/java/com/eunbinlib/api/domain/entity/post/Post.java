package com.eunbinlib.api.domain.entity.post;

import com.eunbinlib.api.domain.entity.BaseTimeEntity;
import com.eunbinlib.api.domain.entity.imagefile.PostImageFile;
import com.eunbinlib.api.domain.request.PostEdit;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @Enumerated
    @NotNull
    private PostState state;

    @Lob
    @NotNull
    private String content;

    @NotNull
    private Long likeCount;

    @NotNull
    private Long viewCount;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImageFile> images = new ArrayList<>();

    @Builder
    public Post(String title, String content, Long likeCount, Long viewCount, List<PostImageFile> images) {
        this.title = title;
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

    public void increaseViewCount(){
        viewCount += 1;
    }

    public void increaseLikeCount() { likeCount += 1;}

    public void decreaseLikeCount() {likeCount -= 1;}
}
