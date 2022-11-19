package com.eunbinlib.api.domain.entity.imagefile;

import com.eunbinlib.api.domain.entity.post.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImageFile extends BaseImageFile {

    @ManyToOne
    @JoinColumn(name = "POST_ID")
    private Post post;

    public void setPost(Post post) {
        this.post = post;
        if (!post.getImages().contains(this)) {
            post.getImages().add(this);
        }
    }

}
