package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.entity.post.Post;
import com.eunbinlib.api.domain.request.PostEdit;
import com.eunbinlib.api.domain.request.PostSearch;
import com.eunbinlib.api.domain.request.PostWrite;
import com.eunbinlib.api.domain.response.OnlyId;
import com.eunbinlib.api.domain.response.PaginationMeta;
import com.eunbinlib.api.domain.response.PaginationRes;
import com.eunbinlib.api.domain.response.PostResponse;
import com.eunbinlib.api.exception.type.PostNotFoundException;
import com.eunbinlib.api.repository.post.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
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
        OnlyId onlyId = postService.write(postWrite);

        // then
        assertEquals(1L, postRepository.count());
        Post findPost = postRepository.findById(onlyId.getId()).orElseThrow(IllegalArgumentException::new);
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void readOne() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();

        Post savedPost = postRepository.save(post);

        // when
        PostResponse findPost = postService.read(savedPost.getId());

        // then
        assertNotNull(findPost);
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void readOnePostNotFound() {
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
    @DisplayName("글 페이지네이션 조회 - after null이면 처음부터 조회")
    void readMany() {
        // given
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .after(null)
                .size(5)
                .build();

        // when
        PaginationRes<PostResponse> result = postService.readMany(postSearch);

        PaginationMeta meta = result.getMeta();
        List<PostResponse> data = result.getData();

        // then
        assertEquals(5, meta.getSize());
        assertEquals(true, meta.getHasMore());
        assertEquals("제목29", data.get(0).getTitle());
        assertEquals("내용29", data.get(0).getContent());
        assertEquals("제목25", data.get(data.size() - 1).getTitle());
        assertEquals("내용25", data.get(data.size() - 1).getContent());
    }

    @Test
    @DisplayName("글 페이지네이션 조회 - 기본값으로 조회")
    void readManyDefaultValue() {
        // given
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .build();

        // when
        PaginationRes<PostResponse> result = postService.readMany(postSearch);

        PaginationMeta meta = result.getMeta();
        List<PostResponse> data = result.getData();

        // then
        assertEquals(20, meta.getSize());
        assertEquals(true, meta.getHasMore());
        assertEquals("제목29", data.get(0).getTitle());
        assertEquals("내용29", data.get(0).getContent());
        assertEquals("제목10", data.get(data.size() - 1).getTitle());
        assertEquals("내용10", data.get(data.size() - 1).getContent());
    }

    @Test
    @DisplayName("글 페이지네이션 조회 - after 이후부터 조회")
    void readManyAfter() {
        // given
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        List<Post> posts = postRepository.findAll();
        Post title15 = posts.stream()
                .filter(post -> post.getTitle().equals("제목15"))
                .findFirst().orElseThrow();

        PostSearch postSearch = PostSearch.builder()
                .after(title15.getId())
                .size(10)
                .build();

        // when
        PaginationRes<PostResponse> result = postService.readMany(postSearch);

        PaginationMeta meta = result.getMeta();
        List<PostResponse> data = result.getData();

        // then
        assertEquals(10, meta.getSize());
        assertEquals(true, meta.getHasMore());
        assertEquals("제목14", data.get(0).getTitle());
        assertEquals("내용14", data.get(0).getContent());
        assertEquals("제목5", data.get(data.size() - 1).getTitle());
        assertEquals("내용5", data.get(data.size() - 1).getContent());
    }

    @Test
    @DisplayName("글 페이지네이션 조회 - 더 이상 조회 불가능 (남아있는 개수가 요청 개수보다 적을 때)")
    void readManyNoMore() {
        // given
        List<Post> requestPosts = IntStream.range(0, 10)
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .size(20)
                .build();

        // when
        PaginationRes<PostResponse> result = postService.readMany(postSearch);

        PaginationMeta meta = result.getMeta();
        List<PostResponse> data = result.getData();

        // then
        assertEquals(10, meta.getSize());
        assertEquals(false, meta.getHasMore());
        assertEquals("제목9", data.get(0).getTitle());
        assertEquals("내용9", data.get(0).getContent());
        assertEquals("제목0", data.get(data.size() - 1).getTitle());
        assertEquals("내용0", data.get(data.size() - 1).getContent());
    }

    @Test
    @DisplayName("글 페이지네이션 조회 - 더 이상 조회 불가능 (남아있는 개수 == 요청 개수)")
    void readManyNoMoreEdgeCase() {
        // given
        List<Post> requestPosts = IntStream.range(0, 10)
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .size(10)
                .build();

        // when
        PaginationRes<PostResponse> result = postService.readMany(postSearch);

        PaginationMeta meta = result.getMeta();
        List<PostResponse> data = result.getData();

        // then
        assertEquals(10, meta.getSize());
        assertEquals(false, meta.getHasMore());
        assertEquals("제목9", data.get(0).getTitle());
        assertEquals("내용9", data.get(0).getContent());
        assertEquals("제목0", data.get(data.size() - 1).getTitle());
        assertEquals("내용0", data.get(data.size() - 1).getContent());
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
    void editPostNotFound() {
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
    void deletePostNotFound() {
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