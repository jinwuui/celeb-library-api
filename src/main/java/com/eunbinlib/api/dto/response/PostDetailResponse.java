package com.eunbinlib.api.dto.response;

import com.eunbinlib.api.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDetailResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final Long viewCount;
    private final Long likeCount;

    @Builder
    public PostDetailResponse(Long id, String title, String content, Long viewCount, Long likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
    }

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .build();
    }

}
