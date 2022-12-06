package com.eunbinlib.api.dto.response.postdetailresponse;

import com.eunbinlib.api.domain.imagefile.PostImageFile;
import com.eunbinlib.api.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PostInfo {

    private final Long id;

    private final String title;

    private final String content;

    private final LocalDateTime createdDate;

    private final List<String> postImageUrls;

    private final Long viewCount;

    private final Long likeCount;

    private final WriterInfo writer;

    public static PostInfo from(final Post post) {
        WriterInfo writerInfo = WriterInfo.from(post.getMember());

        List<String> postImageUrls = generatePostImageUrls(post.getImages());

        return PostInfo.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdDate(post.getCreatedDate())
                .postImageUrls(postImageUrls)
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .writer(writerInfo)
                .build();
    }

    private static List<String> generatePostImageUrls(final List<PostImageFile> images) {
        // TODO: change to AWS S3 URL
        return images.stream()
                .map(e -> e.getBaseImageFile().getStoredFilename())
                .collect(Collectors.toList());
    }
}
