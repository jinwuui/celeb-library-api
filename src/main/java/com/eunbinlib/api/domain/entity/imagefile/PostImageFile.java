package com.eunbinlib.api.domain.entity.imagefile;

import com.eunbinlib.api.domain.entity.post.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImageFile extends BaseImageFile {

    @ManyToOne
    @NotNull
    @JoinColumn(name = "POST_ID")
    private Post post;

}
