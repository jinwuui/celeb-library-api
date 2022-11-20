package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.entity.imagefile.PostImageFile;
import com.eunbinlib.api.domain.entity.post.Post;
import com.eunbinlib.api.domain.entity.post.PostState;
import com.eunbinlib.api.domain.entity.user.Member;
import com.eunbinlib.api.domain.request.PostEdit;
import com.eunbinlib.api.domain.request.PostSearch;
import com.eunbinlib.api.domain.request.PostWrite;
import com.eunbinlib.api.domain.response.*;
import com.eunbinlib.api.exception.type.PostNotFoundException;
import com.eunbinlib.api.repository.post.PostRepository;
import com.eunbinlib.api.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostImageFileRepository postImageFileRepository;

    @Autowired
    UserRepository userRepository;

    Member mockMember;

    @BeforeEach
    void clean() {

        postRepository.deleteAll();
        userRepository.deleteAll();

        mockMember = userRepository.save(Member.builder()
                .username("mockMember")
                .nickname("mockMember")
                .password("mockPassword")
                .build()
        );
    }

    @Test
    @DisplayName("글 작성 - 이미지 없음")
    void writeNoImages() {
        // given
        PostWrite postWrite = PostWrite.builder()
                .title("제목")
                .content("내용")
                .build();

        // when
        OnlyId onlyId = postService.write(mockMember.getId(), postWrite);

        // then
        assertThat(postRepository.count()).isEqualTo(1L);

        Post findPost = postRepository.findById(onlyId.getId()).orElseThrow(IllegalArgumentException::new);
        assertThat(findPost.getTitle()).isEqualTo("제목");
        assertThat(findPost.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("글 작성 - 이미지 있음")
    void writeWithImages() {
        // given
        List<MultipartFile> images = List.of(
                new MockMultipartFile("images", "test1.png", MediaType.IMAGE_PNG_VALUE, "<<png data>>".getBytes()),
                new MockMultipartFile("images", "test2.jpg", MediaType.IMAGE_PNG_VALUE, "<<jpg data>>".getBytes())
        );

        PostWrite postWrite = PostWrite.builder()
                .title("제목")
                .content("내용")
                .images(images)
                .build();

        // when
        OnlyId onlyId = postService.write(mockMember.getId(), postWrite);

        // then
        assertThat(postRepository.count()).isEqualTo(1L);

        Post findPost = postRepository.findById(onlyId.getId())
                .orElseThrow(IllegalArgumentException::new);
        assertThat(findPost.getTitle()).isEqualTo("제목");
        assertThat(findPost.getContent()).isEqualTo("내용");

        List<PostImageFile> findImages = postImageFileRepository.findAllByPostId(findPost.getId())
                .orElseThrow(IllegalArgumentException::new);
        assertThat(findImages.size()).isEqualTo(2L);
    }


    @Test
    @DisplayName("글 1개 조회")
    void readOne() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);

        Post savedPost = postRepository.save(post);

        // when
        PostDetailRes findPost = postService.read(savedPost.getId());

        // then
        assertThat(findPost).isNotNull();
        assertThat(findPost.getTitle()).isEqualTo("제목");
        assertThat(findPost.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void readOnePostNotFound() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);
        postRepository.save(post);

        // expected
        assertThatThrownBy(() -> postService.read(post.getId() + 1L))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("글 페이지네이션 조회 - after null이면 처음부터 조회")
    void readMany() {
        // given
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> {
                    Post post = Post.builder()
                            .title("제목" + i)
                            .content("내용" + i)
                            .state(PostState.NORMAL)
                            .build();
                    post.setMember(mockMember);
                    return post;
                })
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .after(null)
                .size(5)
                .build();

        // when
        PaginationRes<PostRes> result = postService.readMany(postSearch);

        PaginationMeta meta = result.getMeta();
        List<PostRes> data = result.getData();

        // then
        assertThat(meta.getSize()).isEqualTo(5);
        assertThat(meta.getHasMore()).isEqualTo(true);
        assertThat(data.get(0).getTitle()).isEqualTo("제목29");
        assertThat(data.get(0).getContent()).isEqualTo("내용29");
        assertThat(data.get(data.size() - 1).getTitle()).isEqualTo("제목25");
        assertThat(data.get(data.size() - 1).getContent()).isEqualTo("내용25");
    }

    @Test
    @DisplayName("글 페이지네이션 조회 - 기본값으로 조회")
    void readManyDefaultValue() {
        // given
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> {
                    Post post = Post.builder()
                            .title("제목" + i)
                            .content("내용" + i)
                            .state(PostState.NORMAL)
                            .build();
                    post.setMember(mockMember);
                    return post;
                })
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .build();

        // when
        PaginationRes<PostRes> result = postService.readMany(postSearch);

        PaginationMeta meta = result.getMeta();
        List<PostRes> data = result.getData();

        // then
        assertThat(meta.getSize()).isEqualTo(20);
        assertThat(meta.getHasMore()).isEqualTo(true);
        assertThat(data.get(0).getTitle()).isEqualTo("제목29");
        assertThat(data.get(0).getContent()).isEqualTo("내용29");
        assertThat(data.get(data.size() - 1).getTitle()).isEqualTo("제목10");
        assertThat(data.get(data.size() - 1).getContent()).isEqualTo("내용10");
    }

    @Test
    @DisplayName("글 페이지네이션 조회 - after 이후부터 조회")
    void readManyAfter() {
        // given
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> {
                    Post post = Post.builder()
                            .title("제목" + i)
                            .content("내용" + i)
                            .state(PostState.NORMAL)
                            .build();
                    post.setMember(mockMember);
                    return post;
                })
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
        PaginationRes<PostRes> result = postService.readMany(postSearch);

        PaginationMeta meta = result.getMeta();
        List<PostRes> data = result.getData();

        // then
        assertThat(meta.getSize()).isEqualTo(10);
        assertThat(meta.getHasMore()).isEqualTo(true);
        assertThat(data.get(0).getTitle()).isEqualTo("제목14");
        assertThat(data.get(0).getContent()).isEqualTo("내용14");
        assertThat(data.get(data.size() - 1).getTitle()).isEqualTo("제목5");
        assertThat(data.get(data.size() - 1).getContent()).isEqualTo("내용5");
    }

    @Test
    @DisplayName("글 페이지네이션 조회 - 더 이상 조회 불가능 (남아있는 개수가 요청 개수보다 적을 때)")
    void readManyNoMore() {
        // given
        List<Post> requestPosts = IntStream.range(0, 10)
                .mapToObj(i -> {
                    Post post = Post.builder()
                            .title("제목" + i)
                            .content("내용" + i)
                            .state(PostState.NORMAL)
                            .build();
                    post.setMember(mockMember);
                    return post;
                })
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .size(20)
                .build();

        // when
        PaginationRes<PostRes> result = postService.readMany(postSearch);

        PaginationMeta meta = result.getMeta();
        List<PostRes> data = result.getData();

        // then
        assertThat(meta.getSize()).isEqualTo(10);
        assertThat(meta.getHasMore()).isEqualTo(false);
        assertThat(data.get(0).getTitle()).isEqualTo("제목9");
        assertThat(data.get(0).getContent()).isEqualTo("내용9");
        assertThat(data.get(data.size() - 1).getTitle()).isEqualTo("제목0");
        assertThat(data.get(data.size() - 1).getContent()).isEqualTo("내용0");
    }

    @Test
    @DisplayName("글 페이지네이션 조회 - 더 이상 조회 불가능 (남아있는 개수 == 요청 개수)")
    void readManyNoMoreEdgeCase() {
        // given
        List<Post> requestPosts = IntStream.range(0, 10)
                .mapToObj(i -> {
                    Post post = Post.builder()
                            .title("제목" + i)
                            .content("내용" + i)
                            .state(PostState.NORMAL)
                            .build();
                    post.setMember(mockMember);
                    return post;
                })
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .size(10)
                .build();

        // when
        PaginationRes<PostRes> result = postService.readMany(postSearch);

        PaginationMeta meta = result.getMeta();
        List<PostRes> data = result.getData();

        // then
        assertThat(meta.getSize()).isEqualTo(10);
        assertThat(meta.getHasMore()).isEqualTo(false);
        assertThat(data.get(0).getTitle()).isEqualTo("제목9");
        assertThat(data.get(0).getContent()).isEqualTo("내용9");
        assertThat(data.get(data.size() - 1).getTitle()).isEqualTo("제목0");
        assertThat(data.get(data.size() - 1).getContent()).isEqualTo("내용0");
    }

    @Test
    @DisplayName("글 제목 수정")
    void editTitle() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);
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

        assertThat(editedPost.getTitle()).isEqualTo("수정된제목");
        assertThat(editedPost.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("글 내용 수정")
    void editContent() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);
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

        assertThat(editedPost.getTitle()).isEqualTo("제목");
        assertThat(editedPost.getContent()).isEqualTo("수정된내용");
    }

    @Test
    @DisplayName("글 수정 - 존재하지 않는 글")
    void editPostNotFound() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("수정된제목")
                .content("수정된내용")
                .build();

        // when
        assertThatThrownBy(() -> postService.edit(post.getId() + 1L, postEdit))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("글 삭제")
    void delete() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);
        postRepository.save(post);

        // when
        postService.delete(post.getId());

        // then
        assertThat(postRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("글 삭제 - 존재하지 않는 글")
    void deletePostNotFound() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);
        postRepository.save(post);

        // expected
        assertThatThrownBy(() -> postService.delete(post.getId() + 1))
                .isInstanceOf(PostNotFoundException.class);
    }

}