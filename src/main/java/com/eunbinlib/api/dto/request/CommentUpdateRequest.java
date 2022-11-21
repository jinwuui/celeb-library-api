package com.eunbinlib.api.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentUpdateRequest {

    @NotBlank(message = "댓글을 입력해주세요.")
    private final String content;

    @Builder
    public CommentUpdateRequest(String content) {
        this.content = content;
    }
}
