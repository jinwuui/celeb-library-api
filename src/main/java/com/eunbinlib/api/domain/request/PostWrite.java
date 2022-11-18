package com.eunbinlib.api.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class PostWrite {

    @NotBlank(message = "제목을 입력해주세요.")
    private final String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private final String content;

    private final List<MultipartFile> images;

    @Builder
    public PostWrite(String title, String content, List<MultipartFile> images) {
        this.title = title;
        this.content = content;
        this.images = images;
    }

    /**
     * 단순 필드 검증이 아닌 복합/예외 검증시 사용
     */
    public void validate() {
//        if (images.isEmpty()) {
//            throw new InvalidRequestException();
//        }
    }

}
