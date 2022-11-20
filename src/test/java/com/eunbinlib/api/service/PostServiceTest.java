package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.imagefile.PostImageFile;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.dto.response.*;
import com.eunbinlib.api.exception.type.PostNotFoundException;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
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
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("제목")
                .content("내용")
                .build();

        // when
        OnlyIdResponse onlyIdResponse = postService.create(mockMember.getId(), postCreateRequest);

        // then
        assertThat(postRepository.count()).isEqualTo(1L);

        Post findPost = postRepository.findById(onlyIdResponse.getId()).orElseThrow(IllegalArgumentException::new);
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

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("제목")
                .content("내용")
                .images(images)
                .build();

        // when
        OnlyIdResponse onlyIdResponse = postService.create(mockMember.getId(), postCreateRequest);

        // then
        assertThat(postRepository.count()).isEqualTo(1L);

        Post findPost = postRepository.findById(onlyIdResponse.getId())
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
        PostDetailResposne findPost = postService.read(savedPost.getId());

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

        PostReadRequest postReadRequest = PostReadRequest.builder()
                .after(null)
                .size(5)
                .build();

        // when
        PaginationResponse<PostResponse> result = postService.readMany(postReadRequest);

        PaginationMeta meta = result.getMeta();
        List<PostResponse> data = result.getData();

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

        PostReadRequest postReadRequest = PostReadRequest.builder()
                .build();

        // when
        PaginationResponse<PostResponse> result = postService.readMany(postReadRequest);

        PaginationMeta meta = result.getMeta();
        List<PostResponse> data = result.getData();

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

        PostReadRequest postReadRequest = PostReadRequest.builder()
                .after(title15.getId())
                .size(10)
                .build();

        // when
        PaginationResponse<PostResponse> result = postService.readMany(postReadRequest);

        PaginationMeta meta = result.getMeta();
        List<PostResponse> data = result.getData();

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

        PostReadRequest postReadRequest = PostReadRequest.builder()
                .size(20)
                .build();

        // when
        PaginationResponse<PostResponse> result = postService.readMany(postReadRequest);

        PaginationMeta meta = result.getMeta();
        List<PostResponse> data = result.getData();

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

        PostReadRequest postReadRequest = PostReadRequest.builder()
                .size(10)
                .build();

        // when
        PaginationResponse<PostResponse> result = postService.readMany(postReadRequest);

        PaginationMeta meta = result.getMeta();
        List<PostResponse> data = result.getData();

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

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("수정된제목")
                .content("내용")
                .build();

        // when
        postService.update(post.getId(), postUpdateRequest);

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

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("제목")
                .content("수정된내용")
                .build();

        // when
        postService.update(post.getId(), postUpdateRequest);

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

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("수정된제목")
                .content("수정된내용")
                .build();

        // when
        assertThatThrownBy(() -> postService.update(post.getId() + 1L, postUpdateRequest))
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
        postService.delete(mockMember.getId(), post.getId());

        // then
        assertThat(postRepository.count())
                .isEqualTo(0);
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
        assertThatThrownBy(
                () -> postService.delete(mockMember.getId(), post.getId() + 1L))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("다른 사람의 글을 삭제하는 경우")
    void deletePostOfAnotherUser() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);
        postRepository.save(post);

        // expected
        assertThatThrownBy(
                () -> postService.delete(mockMember.getId() + 1L, post.getId()))
                .isInstanceOf(UnauthorizedException.class);
    }

}