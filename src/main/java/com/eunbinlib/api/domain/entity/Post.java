package com.eunbinlib.api.domain.entity;

import com.eunbinlib.api.domain.request.PostEdit;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void edit(PostEdit postEdit) {
        PostEdit fixedPostEdit = PostEdit.builder()
                .title(postEdit.getTitle() != null ? postEdit.getTitle() : title)
                .content(postEdit.getContent() != null ? postEdit.getContent() : content)
                .build();

        title = fixedPostEdit.getTitle();
        content = fixedPostEdit.getContent();
    }

}
