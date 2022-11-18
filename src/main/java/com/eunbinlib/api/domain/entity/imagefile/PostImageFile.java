package com.eunbinlib.api.domain.entity.imagefile;

import com.eunbinlib.api.domain.entity.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String fileName;

    @NotNull
    private String extension;

    @NotNull
    private Integer widthPixel;

    @NotNull
    private Integer heightPixel;

    @NotNull
    private Integer byteSize;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "POST_ID")
    private Post post;

    @Builder
    public PostImageFile(String fileName, String extension, Integer widthPixel, Integer heightPixel, Integer byteSize) {
        this.fileName = fileName;
        this.extension = extension;
        this.widthPixel = widthPixel;
        this.heightPixel = heightPixel;
        this.byteSize = byteSize;
    }
}
