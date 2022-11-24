package com.eunbinlib.api.service;

import com.eunbinlib.api.ServiceTest;
import com.eunbinlib.api.domain.imagefile.PostImageFile;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.dto.response.*;
import com.eunbinlib.api.exception.type.auth.UnauthorizedException;
import com.eunbinlib.api.exception.type.notfound.PostNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class PostServiceTest extends ServiceTest {

    @Autowired
    PostService postService;

    @Nested
    @DisplayName("글 작성 테스트")
    class Create {

        @Test
        @DisplayName("글 작성 - 이미지 없음")
        void writeNoImages() {
            // given
            Member member = getMember();
            PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                    .title("제목")
                    .content("내용")
                    .build();

            // when
            OnlyIdResponse onlyIdResponse = postService.create(member.getId(), postCreateRequest);

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
            Member member = getMember();
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
            OnlyIdResponse onlyIdResponse = postService.create(member.getId(), postCreateRequest);

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
    }

    @Nested
    @DisplayName("글 조회 테스트")
    class Read {
        @Test
        @DisplayName("글 1개 조회")
        void readOne() {
            // given
            Member member = getMember();
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .state(PostState.NORMAL)
                    .build();
            post.setMember(member);

            Post savedPost = postRepository.save(post);

            // when
            PostDetailResposne findPost = postService.readDetail(savedPost.getId());

            // then
            assertThat(findPost).isNotNull();
            assertThat(findPost.getTitle()).isEqualTo("제목");
            assertThat(findPost.getContent()).isEqualTo("내용");
        }

        @Test
        @DisplayName("글 1개 조회 - 존재하지 않는 글")
        void readOnePostNotFound() {
            // given
            Member member = getMember();
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .state(PostState.NORMAL)
                    .build();
            post.setMember(member);
            postRepository.save(post);

            // expected
            assertThatThrownBy(() -> postService.readDetail(post.getId() + 1L))
                    .isInstanceOf(PostNotFoundException.class);
        }

        @Test
        @DisplayName("삭제된 글을 조회하는 경우")
        void readOneDeletedPost() {
            // given
            Member member = getMember();
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .state(PostState.DELETED)
                    .build();
            post.setMember(member);
            postRepository.save(post);

            // expected
            assertThatThrownBy(() -> postService.readDetail(post.getId()))
                    .isInstanceOf(PostNotFoundException.class);
        }

        @Test
        @DisplayName("글 페이지네이션 조회 - after null이면 처음부터 조회")
        void readMany() {
            // given
            Member member = getMember();
            List<Post> requestPosts = IntStream.range(0, 30)
                    .mapToObj(i -> {
                        Post post = Post.builder()
                                .title("제목" + i)
                                .content("내용" + i)
                                .state(PostState.NORMAL)
                                .build();
                        post.setMember(member);
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
            Member member = getMember();
            List<Post> requestPosts = IntStream.range(0, 30)
                    .mapToObj(i -> {
                        Post post = Post.builder()
                                .title("제목" + i)
                                .content("내용" + i)
                                .state(PostState.NORMAL)
                                .build();
                        post.setMember(member);
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
            Member member = getMember();
            List<Post> requestPosts = IntStream.range(0, 30)
                    .mapToObj(i -> {
                        Post post = Post.builder()
                                .title("제목" + i)
                                .content("내용" + i)
                                .state(PostState.NORMAL)
                                .build();
                        post.setMember(member);
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
            Member member = getMember();
            List<Post> requestPosts = IntStream.range(0, 10)
                    .mapToObj(i -> {
                        Post post = Post.builder()
                                .title("제목" + i)
                                .content("내용" + i)
                                .state(PostState.NORMAL)
                                .build();
                        post.setMember(member);
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
            Member member = getMember();
            List<Post> requestPosts = IntStream.range(0, 10)
                    .mapToObj(i -> {
                        Post post = Post.builder()
                                .title("제목" + i)
                                .content("내용" + i)
                                .state(PostState.NORMAL)
                                .build();
                        post.setMember(member);
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
        @DisplayName("글 페이지네이션 조회 - 삭제된 게시글을 제외하고 조회하는 경우")
        void readManyMixedDeletedPosts() {
            // given
            Member member = getMember();
            List<Post> requestPosts = IntStream.range(0, 10)
                    .mapToObj(i -> {
                        Post post = Post.builder()
                                .title("제목" + i)
                                .content("내용" + i)
                                .state(i % 2 == 0 ? PostState.NORMAL : PostState.DELETED)
                                .build();
                        post.setMember(member);
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
            assertThat(meta.getSize()).isEqualTo(5);
            assertThat(meta.getHasMore()).isEqualTo(false);
            assertThat(data.get(0).getTitle()).isEqualTo("제목8");
            assertThat(data.get(0).getContent()).isEqualTo("내용8");
            assertThat(data.get(data.size() - 1).getTitle()).isEqualTo("제목0");
            assertThat(data.get(data.size() - 1).getContent()).isEqualTo("내용0");
        }
    }

    @Nested
    @DisplayName("글 수정 테스트")
    class Update {


    }
    @Nested
    @DisplayName("글 삭제 테스트")
    class Delete {


    }




    @Test
    @DisplayName("글 제목 수정")
    void updateTitle() {
        // given
        Member member = getMember();
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(member);
        postRepository.save(post);

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("수정된제목")
                .content("내용")
                .build();

        // when
        postService.update(member.getId(), post.getId(), postUpdateRequest);

        // then
        Post updatedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));

        assertThat(updatedPost.getTitle()).isEqualTo("수정된제목");
        assertThat(updatedPost.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("글 내용 수정")
    void updateContent() {
        // given
        Member member = getMember();
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(member);
        postRepository.save(post);

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("제목")
                .content("수정된내용")
                .build();

        // when
        postService.update(member.getId(), post.getId(), postUpdateRequest);

        // then
        Post updatedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));

        assertThat(updatedPost.getTitle()).isEqualTo("제목");
        assertThat(updatedPost.getContent()).isEqualTo("수정된내용");
    }

    @Test
    @DisplayName("글 수정 - 존재하지 않는 글")
    void updatePostNotFound() {
        // given
        Member member = getMember();
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(member);
        postRepository.save(post);

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("수정된제목")
                .content("수정된내용")
                .build();

        // when
        assertThatThrownBy(() -> postService.update(member.getId(), post.getId() + 1L, postUpdateRequest))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("다른 사람의 글을 수정하는 경우")
    void updatePostOfAnotherUser() {
        // given
        Member member = getMember();
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(member);
        postRepository.save(post);

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("수정된제목")
                .content("수정된내용")
                .build();

        // when
        assertThatThrownBy(() -> postService.update(member.getId() + 1L, post.getId(), postUpdateRequest))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("글 삭제")
    void delete() {
        // given
        Member member = getMember();
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(member);
        postRepository.save(post);

        // when
        postService.delete(member.getId(), post.getId());

        // then
        Post findPost = postRepository.findById(post.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertThat(postRepository.count())
                .isEqualTo(1L);
        assertThat(findPost.getState())
                .isEqualTo(PostState.DELETED);
    }

    @Test
    @DisplayName("글 삭제 - 존재하지 않는 글")
    void deletePostNotFound() {
        // given
        Member member = getMember();
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(member);
        postRepository.save(post);

        // expected
        assertThatThrownBy(
                () -> postService.delete(member.getId(), post.getId() + 1L))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("다른 사람의 글을 삭제하는 경우")
    void deletePostOfAnotherUser() {
        // given
        Member member = getMember();
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(member);
        postRepository.save(post);

        // expected
        assertThatThrownBy(
                () -> postService.delete(member.getId() + 1L, post.getId()))
                .isInstanceOf(UnauthorizedException.class);
    }

}