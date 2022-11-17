package com.eunbinlib.api.domain.response;

import com.eunbinlib.api.domain.entity.post.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDetailRes {

    private final Long id;
    private final String title;
    private final String content;

    @Builder
    public PostDetailRes(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public PostDetailRes(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

}