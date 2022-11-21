package com.eunbinlib.api.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentUpdateRequest {

    @NotBlank(message = "댓글을 입력해주세요.")
    private String content;

    @Builder
    public CommentUpdateRequest(String content) {
        this.content = content;
    }
}
