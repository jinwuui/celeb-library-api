package com.eunbinlib.api.application.domain.imagefile;

import com.eunbinlib.api.application.domain.BaseTimeEntity;
import com.eunbinlib.api.application.domain.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImageFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Embedded
    private BaseImageFile baseImageFile;

    @ManyToOne
    @JoinColumn(name = "POST_ID")
    private Post post;

    @Builder
    public PostImageFile(final BaseImageFile baseImageFile, final Post post) {
        this.baseImageFile = baseImageFile;
        this.post = post;
    }

    public void setPost(final Post post) {
        this.post = post;
        if (post != null && !post.getImages().contains(this)) {
            post.getImages().add(this);
        }
    }
}
