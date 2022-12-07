package com.eunbinlib.api.domain.imagefile;

import com.eunbinlib.api.domain.post.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostImageFileTest {

    @Test
    @DisplayName("게시글 사진 파일에 게시글 설정")
    void setPostSuccess() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();

        PostImageFile postImageFile = PostImageFile.builder()
                .baseImageFile(new BaseImageFile())
                .build();

        // when
        postImageFile.setPost(post);

        // then
        assertThat(postImageFile.getPost())
                .isEqualTo(post);
        assertThat(post.getImages().contains(postImageFile))
                .isTrue();
    }
}
