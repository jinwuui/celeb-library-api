package com.eunbinlib.api.dto.response.postdetailresponse;

import com.eunbinlib.api.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentInfo {

    private final Long id;

    private final String content;

    private final LocalDateTime createdDate;

    private final WriterInfo writer;

    public static CommentInfo from(final Comment comment) {
        return CommentInfo.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .writer(WriterInfo.from(comment.getMember()))
                .build();
    }
}
