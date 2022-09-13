package com.eunbinlib.api.domain.request;

import com.eunbinlib.api.exception.type.InvalidRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class PostWrite {

    @NotBlank(message = "제목을 입력해주세요.")
    private final String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private final String content;

    // TODO: 사진 데이터 필요 (게시글 등록 시, 박은빈 사진 필수)
    // NOTE: 사진 데이터 통신은 multipart?
//     private final List<String> images;

    @Builder
    public PostWrite(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void validate() {
        /**
         *  단순 필드 검증이 아닌 복합/예외 검증시 사용
         */
//        if (images.isEmpty()) {
//            throw new InvalidRequestException();
//        }
    }
}
