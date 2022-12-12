package com.eunbinlib.api.application.dto.response.postdetailresponse;

import com.eunbinlib.api.application.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PostDetailResponse {

    private final PostInfo post;

    private final List<CommentInfo> comments;

    public static PostDetailResponse from(final Post post) {
        PostInfo postInfo = PostInfo.from(post);

        List<CommentInfo> commentInfos = post.getComments()
                .stream()
                .map(CommentInfo::from)
                .collect(Collectors.toList());

        return PostDetailResponse.builder()
                .post(postInfo)
                .comments(commentInfos)
                .build();
    }
}
