package com.eunbinlib.api.dto.response;

import com.eunbinlib.api.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDetailResposne {

    private final Long id;
    private final String title;
    private final String content;

    @Builder
    public PostDetailResposne(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public PostDetailResposne(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

}
