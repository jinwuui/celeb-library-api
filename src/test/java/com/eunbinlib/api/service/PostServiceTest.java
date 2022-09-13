package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.entity.Post;
import com.eunbinlib.api.domain.request.PostEdit;
import com.eunbinlib.api.domain.request.PostSearch;
import com.eunbinlib.api.domain.request.PostWrite;
import com.eunbinlib.api.domain.response.PostResponse;
import com.eunbinlib.api.exception.type.PostNotFoundException;
import com.eunbinlib.api.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void write() {
        // given
        PostWrite postWrite = PostWrite.builder()
                .title("제목")
                .content("내용")
                .build();

        // when
        Long postId = postService.write(postWrite);

        // then
        assertEquals(1L, postRepository.count());
        Post findPost = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void readOne() {
        // given
        PostWrite postWrite = PostWrite.builder()
                .title("제목")
                .content("내용")
                .build();

        Long postId = postService.write(postWrite);

        // when
        PostResponse findPost = postService.read(postId);

        // then
        assertNotNull(findPost);
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void readOneFail() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        // expected
        assertThrows(PostNotFoundException.class, () -> {
            postService.read(post.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void readMany() {
        // given
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .build();

        // when
        List<PostResponse> posts = postService.readMany(postSearch);

        // then
        assertEquals(10L, posts.size());
        assertEquals("제목19", posts.get(0).getTitle());
    }

    @Test
    @DisplayName("글 페이지 조회 - page <= 0 이면 1페이지 반환")
    void readManyPageLessEqual0() {
        // given
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .page(0)
                .build();

        // when
        List<PostResponse> posts = postService.readMany(postSearch);

        // then
        assertEquals(10L, posts.size());
        assertEquals("제목19", posts.get(0).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void editTitle() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("수정된제목")
                .content("내용")
                .build();

        // when
        postService.edit(post.getId(), postEdit);

        // then
        Post editedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));

        assertEquals("수정된제목", editedPost.getTitle());
        assertEquals("내용", editedPost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정")
    void editContent() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목")
                .content("수정된내용")
                .build();

        // when
        postService.edit(post.getId(), postEdit);

        // then
        Post editedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));

        assertEquals("제목", editedPost.getTitle());
        assertEquals("수정된내용", editedPost.getContent());
    }

    @Test
    @DisplayName("글 수정 - 존재하지 않는 글")
    void editFail() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("수정된제목")
                .content("수정된내용")
                .build();

        // when
        assertThrows(PostNotFoundException.class, () -> {
            postService.edit(post.getId() + 1L, postEdit);
        });
    }

    @Test
    @DisplayName("글 삭제")
    void delete() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        // when
        postService.delete(post.getId());

        // then
        assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("글 삭제 - 존재하지 않는 글")
    void deleteFail() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        // expected
        assertThrows(PostNotFoundException.class, () -> {
            postService.delete(post.getId() + 1);
        });
    }
}