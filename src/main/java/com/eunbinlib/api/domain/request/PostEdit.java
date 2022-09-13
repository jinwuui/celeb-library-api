package com.eunbinlib.api.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PostEdit {

    @NotBlank(message = "제목을 입력해주세요.")
    private final String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private final String content;

    // TODO 사진도 수정 가능
//    private final List<Image> images;

    @Builder
    public PostEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
