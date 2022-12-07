package com.eunbinlib.api.domain.post;

import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.user.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class PostTest {

    String title = "제목";
    String content = "내용";

    @Test
    @DisplayName("게시글의 제목만 수정하는 경우")
    void updateOnlyTitle() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        // when
        String newTitle = "새 제목";
        post.updateTitleAndContent(newTitle, null);

        // then
        assertThat(post.getTitle())
                .isEqualTo(newTitle);
        assertThat(post.getContent())
                .isEqualTo(content);
    }

    @Test
    @DisplayName("게시글의 내용만 수정하는 경우")
    void updateOnlyContent() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        // when
        String newContent = "새 내용";
        post.updateTitleAndContent(null, newContent);

        // then
        assertThat(post.getContent())
                .isEqualTo(newContent);
        assertThat(post.getTitle())
                .isEqualTo(title);
    }

    @Test
    @DisplayName("게시글의 제목/내용을 null 값으로 수정하는 경우")
    void updateNullTitleAndNullContent() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        // when
        post.updateTitleAndContent(null, null);

        // then
        assertThat(post.getTitle())
                .isEqualTo(title);
        assertThat(post.getContent())
                .isEqualTo(content);
    }

    @Test
    @DisplayName("게시글의 이미지 수정하는 경우 - 기존 이미지가 존재하지 않을 때")
    void updateImagesNotExistImages() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        List<BaseImageFile> newImages = IntStream.range(0, 5)
                .mapToObj(i -> BaseImageFile.builder().build())
                .collect(Collectors.toList());

        // when
        post.updateImages(null, newImages);

        // then
        assertThat(post.getImages().size())
                .isEqualTo(newImages.size());
    }

    @Test
    @DisplayName("게시글의 이미지 수정하는 경우 - 기존 이미지가 존재할 때")
    void updateImagesExistImages() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();
        post.addImage(BaseImageFile.builder().build());

        List<BaseImageFile> newImages = IntStream.range(0, 5)
                .mapToObj(i -> BaseImageFile.builder().build())
                .collect(Collectors.toList());

        // when
        post.updateImages(null, newImages);

        // then
        assertThat(post.getImages().size())
                .isEqualTo(newImages.size() + 1);
    }

    @Test
    @DisplayName("게시글을 삭제 (게시글의 상태를 DELETED로 변경)")
    void delete() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        // when
        post.delete();

        // then
        assertThat(post.getState())
                .isEqualTo(PostState.DELETED);
    }

    @Test
    @DisplayName("게시글의 조회수 증가")
    void increaseViewCount() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .viewCount(0L)
                .build();

        // when
        post.increaseViewCount();

        // then
        assertThat(post.getViewCount())
                .isEqualTo(1L);
    }

    @Test
    @DisplayName("게시글의 좋아요수 증가")
    void increaseLikeCount() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .likeCount(0L)
                .build();

        // when
        post.increaseLikeCount();

        // then
        assertThat(post.getLikeCount())
                .isEqualTo(1L);
    }

    @Test
    @DisplayName("게시글의 좋아요수 감소")
    void decreaseLikeCount() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .likeCount(100L)
                .build();

        // when
        post.decreaseLikeCount();

        // then
        assertThat(post.getLikeCount())
                .isEqualTo(99L);
    }

    @Test
    @DisplayName("좋아요가 0인 게시글의 좋아요수 감소")
    void decreaseLikeCountAlreadyZero() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .likeCount(0L)
                .build();

        // when
        post.decreaseLikeCount();

        // then
        assertThat(post.getLikeCount())
                .isEqualTo(0L);
    }

    @Test
    @DisplayName("게시글에 이미지를 하나 추가하는 경우")
    void addImage() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        // when
        post.addImage(BaseImageFile.builder().build());

        // then
        assertThat(post.getImages().size())
                .isNotZero();
    }

    @Test
    @DisplayName("게시글에 null 이미지를 추가하는 경우")
    void addNullImage() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        // when
        post.addImage(null);

        // then
        assertThat(post.getImages().size())
                .isZero();
    }

    @Test
    @DisplayName("게시글에 이미지 여러개 추가")
    void addImages() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        int size = 10;
        List<BaseImageFile> images = IntStream.range(0, size)
                .mapToObj(i -> BaseImageFile.builder().build())
                .collect(Collectors.toList());

        // when
        post.addImages(images);

        // then
        assertThat(post.getImages().size())
                .isEqualTo(10L);
    }

    @Test
    @DisplayName("게시글에 이미지 여러개 추가 (리스트에 null 이미지가 포함된 경우)")
    void addImagesIncludeNullImage() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        int size = 10;
        List<BaseImageFile> images = IntStream.range(0, size)
                .mapToObj(i -> i % 2 == 0 ? BaseImageFile.builder().build() : null)
                .collect(Collectors.toList());

        // when
        post.addImages(images);

        // then
        assertThat(post.getImages().size())
                .isEqualTo(5L);
    }

    @Test
    @DisplayName("게시글에 댓글을 등록하는 경우")
    void addComment() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        Comment comment = Comment.builder()
                .content("댓글")
                .build();

        // when
        post.addComment(comment);

        // then
        assertThat(post.getComments().contains(comment))
                .isTrue();
        assertThat(comment.getPost())
                .isEqualTo(post);
    }

    @Test
    @DisplayName("게시글에 작성자를 등록")
    void setMember() {
        // given
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();

        Member member = Member.builder()
                .username("아이디")
                .password("비밀번호")
                .nickname("닉네임")
                .build();

        // when
        post.setMember(member);

        // then
        assertThat(post.getMember())
                .isEqualTo(member);
        assertThat(member.getPosts().contains(post))
                .isTrue();
    }
}