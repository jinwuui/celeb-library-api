package com.eunbinlib.api.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
