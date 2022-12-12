package com.eunbinlib.api.controller;

import com.eunbinlib.api.application.domain.block.Block;
import com.eunbinlib.api.application.domain.comment.Comment;
import com.eunbinlib.api.application.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.application.domain.imagefile.PostImageFile;
import com.eunbinlib.api.application.domain.post.Post;
import com.eunbinlib.api.application.domain.postlike.PostLike;
import com.eunbinlib.api.application.domain.user.Member;
import com.eunbinlib.api.application.dto.request.PostCreateRequest;
import com.eunbinlib.api.application.dto.request.PostReadRequest;
import com.eunbinlib.api.application.dto.request.PostUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.eunbinlib.api.auth.data.AuthProperties.AUTHORIZATION_HEADER;
import static com.eunbinlib.api.auth.data.AuthProperties.TOKEN_PREFIX;
import static com.eunbinlib.api.application.dto.request.PostReadRequest.MAX_SIZE;
import static com.eunbinlib.api.testutils.MultiValueMapper.convert;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class PostControllerTest extends ControllerTest {

    @Nested
    @DisplayName("글 등록")
    class Create {

        @Test
        @DisplayName("글 등록 - 이미지 없음")
        void createNoImages() throws Exception {
            // given
            loginMember();
            PostCreateRequest request = new PostCreateRequest("제목", "내용", null);

            // when & expected
            mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                            .param("title", request.getTitle())
                            .param("content", request.getContent())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isCreated())
                    .andDo(print());

            // then
            Post post = postRepository.findAll().get(0);
            assertThat(post.getTitle())
                    .isEqualTo("제목");
            assertThat(post.getContent())
                    .isEqualTo("내용");

            List<PostImageFile> postImageFile = postImageFileRepository.findAllByPostId(post.getId())
                    .orElseThrow(IllegalArgumentException::new);

            assertThat(postImageFile.size())
                    .isEqualTo(0L);
        }

        @Test
        @DisplayName("글 등록 - 이미지 첨부")
        void createWithImages() throws Exception {
            // given
            loginMember();
            List<MultipartFile> images = List.of(
                    new MockMultipartFile("images", "test1.png", MediaType.IMAGE_PNG_VALUE, "<<png data>>".getBytes()),
                    new MockMultipartFile("images", "test2.jpg", MediaType.IMAGE_JPEG_VALUE, "<<jpg data>>".getBytes())
            );

            PostCreateRequest request = new PostCreateRequest("제목", "내용", images);

            // when & expected
            mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                            .file((MockMultipartFile) images.get(0))
                            .file((MockMultipartFile) images.get(1))
                            .param("title", request.getTitle())
                            .param("content", request.getContent())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isCreated())
                    .andDo(print());

            // then
            Post post = postRepository.findAll().get(0);
            assertThat(post.getTitle())
                    .isEqualTo(request.getTitle());
            assertThat(post.getContent())
                    .isEqualTo(request.getContent());

            List<PostImageFile> postImageFile = postImageFileRepository.findAllByPostId(post.getId())
                    .orElseThrow(IllegalArgumentException::new);

            assertThat(postImageFile.size())
                    .isEqualTo(2L);
        }

        @Test
        @DisplayName("글 등록 실패 - 제목 필수 입력")
        void createTitleNotBlank() throws Exception {
            // given
            loginMember();
            PostCreateRequest request = new PostCreateRequest(null, "내용", null);

            // expected
            mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                            .param("title", request.getTitle())
                            .param("content", request.getContent())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
                    .andDo(print());
        }

        @Test
        @DisplayName("글 등록 실패 - 내용 필수 입력")
        void createContentNotBlank() throws Exception {
            // given
            loginMember();
            PostCreateRequest request = new PostCreateRequest("제목", null, null);

            // expected
            mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                            .param("title", request.getTitle())
                            .param("content", request.getContent())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요."))
                    .andDo(print());
        }

        @Test
        @DisplayName("글 등록 실패 - 게스트 유저인 경우")
        void createByGuest() throws Exception {
            // given
            loginGuest();
            PostCreateRequest request = new PostCreateRequest("제목", "내용", null);

            // expected
            mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                            .param("title", request.getTitle())
                            .param("content", request.getContent())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + guestAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("글 조회")
    class Read {

        @Test
        @DisplayName("글 상세 조회 - 댓글이 없는 경우")
        void readDetailNoComments() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            // expected
            mockMvc.perform(get("/api/posts/{postId}", post.getId())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..['post'].id").value(post.getId().intValue()))
                    .andExpect(jsonPath("$..['post'].title").value(post.getTitle()))
                    .andExpect(jsonPath("$..['comments'].size()").value(0))
                    .andDo(print());
        }

        @Test
        @DisplayName("글 상세 조회 - 댓글이 있는 경우")
        void readDetailWithComments() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);
            Comment comment1 = getComment(member, post);
            Comment comment2 = getComment(member, post);

            // expected
            mockMvc.perform(get("/api/posts/{postId}", post.getId())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..['post'].id").value(post.getId().intValue()))
                    .andExpect(jsonPath("$..['post'].title").value(post.getTitle()))
                    .andExpect(jsonPath("$..['comments'].size()").value(2))
                    .andExpect(jsonPath("$..['comments'][0]['writer'].id").value(member.getId().intValue()))
                    .andDo(print());
        }

        @Test
        @DisplayName("글 상세 조회 - 댓글과 사진이 있는 경우")
        void readDetailWithCommentsAndImages() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);
            IntStream.range(0, 5)
                    .forEach(i -> addPostImageFile(post));

            Comment comment1 = getComment(member, post);
            Comment comment2 = getComment(member, post);

            // expected
            mockMvc.perform(get("/api/posts/{postId}", post.getId())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..['post'].id").value(post.getId().intValue()))
                    .andExpect(jsonPath("$..['post'].title").value(post.getTitle()))
                    .andExpect(jsonPath("$..['post'].postImageUrls.size()").value(5))
                    .andExpect(jsonPath("$..['comments'].size()").value(2))
                    .andExpect(jsonPath("$..['comments'][0]['writer'].id").value(member.getId().intValue()))
                    .andDo(print());
        }

        @Test
        @DisplayName("글 여러개 조회")
        void readMany() throws Exception {
            // given
            loginMember();
            List<Post> requestPosts = IntStream.range(0, 20)
                    .mapToObj(i ->
                            Post.builder()
                                    .title("제목" + i)
                                    .content("내용" + i)
                                    .member(member)
                                    .build()
                    )
                    .collect(Collectors.toList());
            postRepository.saveAll(requestPosts);

            PostReadRequest postReadRequest = PostReadRequest.builder()
                    .after(null)
                    .size(5)
                    .build();

            MultiValueMap<String, String> params = convert(objectMapper, postReadRequest);

            // expected
            mockMvc.perform(get("/api/posts")
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..['data'].length()", is(5)))
                    .andExpect(jsonPath("$..['data'][0].title").value("제목19"))
                    .andExpect(jsonPath("$..['data'][0].content").value("내용19"))
                    .andDo(print());
        }

        @Test
        @DisplayName("글 여러개 조회")
        void readManyByGuest() throws Exception {
            // given
            loginMember();
            List<Post> requestPosts = IntStream.range(0, 20)
                    .mapToObj(i ->
                            Post.builder()
                                    .title("제목" + i)
                                    .content("내용" + i)
                                    .member(member)
                                    .build()
                    )
                    .collect(Collectors.toList());
            postRepository.saveAll(requestPosts);

            loginGuest();

            PostReadRequest postReadRequest = PostReadRequest.builder()
                    .after(null)
                    .size(5)
                    .build();

            MultiValueMap<String, String> params = convert(objectMapper, postReadRequest);

            // expected
            mockMvc.perform(get("/api/posts")
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + guestAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..['data'].length()", is(5)))
                    .andExpect(jsonPath("$..['data'][0].title").value("제목19"))
                    .andExpect(jsonPath("$..['data'][0].content").value("내용19"))
                    .andDo(print());
        }

        @Test
        @DisplayName("글 여러개 조회 - page가 0이하, size가 10000")
        void readManyEdgeCase() throws Exception {
            // given
            loginMember();
            List<Post> requestPosts = IntStream.range(0, 2000)
                    .mapToObj(i ->
                            Post.builder()
                                    .title("제목" + i)
                                    .content("내용" + i)
                                    .member(member)
                                    .build()
                    )
                    .collect(Collectors.toList());
            postRepository.saveAll(requestPosts);

            PostReadRequest postReadRequest = PostReadRequest.builder()
                    .after(null)
                    .size(10000)
                    .build();

            MultiValueMap<String, String> params = convert(objectMapper, postReadRequest);

            // expected
            mockMvc.perform(get("/api/posts")
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..['data'].length()", is(MAX_SIZE)))
                    .andExpect(jsonPath("$..['data'][0].title").value("제목1999"))
                    .andExpect(jsonPath("$..['data'][0].content").value("내용1999"));
        }

        @Test
        @DisplayName("존재하지 않는 게시글 조회")
        void readPostNotFound() throws Exception {
            loginMember();

            // expected
            mockMvc.perform(get("/api/posts/{postId}", 1L)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }

        @Test
        @DisplayName("차단한 사용자의 게시글을 제외하고 조회")
        void readManyExcludePostsOfBlockedUser() throws Exception {
            // given
            // - 로그인 + 자신의 게시글 작성
            loginMember();
            Post post = getPost(member);

            // - 차단될 사용자의 게시글 작성
            Member blocked = getMember();
            getPost(blocked);

            // - 사용자 차단
            blockRepository.save(Block.builder()
                    .blocker(member)
                    .blocked(blocked)
                    .build());

            PostReadRequest postReadRequest = PostReadRequest.builder().build();
            MultiValueMap<String, String> params = convert(objectMapper, postReadRequest);

            // expected
            mockMvc.perform(get("/api/posts")
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..['data'].length()", is(1)))
                    .andExpect(jsonPath("$..['data'][0].title").value(post.getTitle()))
                    .andExpect(jsonPath("$..['data'][0].content").value(post.getContent()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("글 수정")
    class Update {

        @Test
        @DisplayName("글 제목 수정")
        void updateTitle() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            PostUpdateRequest request = new PostUpdateRequest("수정된 제목", null, null, null);

            // expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/posts/{postId}", post.getId())
                            .param("title", request.getTitle())
                            .param("content", request.getContent())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            Post findPost = postRepository.findById(post.getId())
                    .orElseThrow(IllegalArgumentException::new);

            assertThat(findPost.getTitle())
                    .isEqualTo(request.getTitle());
            assertThat(findPost.getContent())
                    .isEqualTo(post.getContent());
        }

        @Test
        @DisplayName("글 내용 수정")
        void updateContent() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            PostUpdateRequest request = new PostUpdateRequest(null, "수정된 내용", null, null);

            // expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/posts/{postId}", post.getId())
                            .param("title", request.getTitle())
                            .param("content", request.getContent())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            Post findPost = postRepository.findById(post.getId())
                    .orElseThrow(IllegalArgumentException::new);

            assertThat(findPost.getTitle())
                    .isEqualTo(post.getTitle());
            assertThat(findPost.getContent())
                    .isEqualTo(request.getContent());
        }

        @Test
        @DisplayName("글 이미지 수정")
        void updateImages() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);
            List<MultipartFile> images = List.of(
                    new MockMultipartFile("newImages", "test1.png", MediaType.IMAGE_PNG_VALUE, "<<png data>>".getBytes()),
                    new MockMultipartFile("newImages", "test2.jpg", MediaType.IMAGE_JPEG_VALUE, "<<jpg data>>".getBytes())
            );

            PostUpdateRequest request = new PostUpdateRequest(null, null, null, images);

            // when & expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/posts/{postId}", post.getId())
                            .file((MockMultipartFile) request.getNewImages().get(0))
                            .file((MockMultipartFile) request.getNewImages().get(1))
                            .param("title", request.getTitle())
                            .param("content", request.getContent())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            Post findPost = postRepository.findById(post.getId())
                    .orElseThrow(IllegalArgumentException::new);

            List<PostImageFile> postImageFile = postImageFileRepository.findAllByPostId(findPost.getId())
                    .orElseThrow(IllegalArgumentException::new);

            assertThat(postImageFile.size())
                    .isEqualTo(2L);
        }

        @Test
        @DisplayName("글 이미지 수정 - 기존 이미지는 삭제하고 새로운 이미지를 추가하는 경우")
        void updateImagesDeleteOldImages() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);
            PostImageFile savedImage1 = postImageFileRepository.save(PostImageFile.builder()
                    .post(post)
                    .baseImageFile(BaseImageFile.builder().build())
                    .build());
            PostImageFile savedImage2 = postImageFileRepository.save(PostImageFile.builder()
                    .post(post)
                    .baseImageFile(BaseImageFile.builder().build())
                    .build());

            List<MultipartFile> images = List.of(
                    new MockMultipartFile("newImages", "test1.png", MediaType.IMAGE_PNG_VALUE, "<<png data>>".getBytes()),
                    new MockMultipartFile("newImages", "test2.jpg", MediaType.IMAGE_JPEG_VALUE, "<<jpg data>>".getBytes())
            );

            PostUpdateRequest request = new PostUpdateRequest(null, null, List.of(savedImage1.getId()), images);

            // when & expected
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/posts/{postId}", post.getId())
                            .file((MockMultipartFile) request.getNewImages().get(0))
                            .file((MockMultipartFile) request.getNewImages().get(1))
                            .param("title", request.getTitle())
                            .param("content", request.getContent())
                            .param("deleteIdList", request.getDeleteIdList().get(0).toString())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            Post findPost = postRepository.findById(post.getId())
                    .orElseThrow(IllegalArgumentException::new);

            List<PostImageFile> findImages = postImageFileRepository.findAllByPostId(findPost.getId())
                    .orElseThrow(IllegalArgumentException::new);

            assertThat(findImages.size())
                    .isEqualTo(3L);
        }

        @Test
        @DisplayName("존재하지 않는 게시글 수정")
        void updatePostNotFound() throws Exception {
            // given
            loginMember();
            PostUpdateRequest request = new PostUpdateRequest(null, "수정된 내용", null, null);

            // expected
            mockMvc.perform(patch("/api/posts/{postId}", 1L)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 사람의 글을 수정하는 경우")
        void updatePostOfAnotherUser() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            Member member2 = getMember();

            String memberAccessToken2 = jwtUtils.createAccessToken(member2.getUserType(), member2.getUsername());
            String memberRefreshToken2 = jwtUtils.createRefreshToken(member2.getUserType(), member2.getUsername());

            PostUpdateRequest request = new PostUpdateRequest("수정된 제목", "수정된 내용", null, null);

            // expected
            mockMvc.perform(patch("/api/posts/{postId}", post.getId())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken2)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("글 삭제")
    class Delete {

        @Test
        @DisplayName("글 삭제")
        void deletePost() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            // expected
            mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("존재하지 않는 게시글 삭제")
        void deletePostNotFound() throws Exception {
            // expected
            loginMember();
            mockMvc.perform(delete("/api/posts/{postId}", 1L)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 사람의 글을 삭제하는 경우")
        void deletePostOfAnotherUser() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            Member member2 = getMember();

            String memberAccessToken2 = jwtUtils.createAccessToken(member2.getUserType(), member2.getUsername());
            String memberRefreshToken2 = jwtUtils.createRefreshToken(member2.getUserType(), member2.getUsername());

            // expected
            mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken2)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("글과 관련된 기타 기능")
    class Etc {

        @Test
        @DisplayName("게시글 좋아요 요청 - 기존에 좋아요가 없는 경우")
        void likePost() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            MultiValueMap<String, String> params = convert(Map.of("isLike", "true"));

            // when
            mockMvc.perform(post("/api/posts/{postId}/like", post.getId())
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            assertThat(postLikeRepository.findByMemberIdAndPostId(member.getId(), post.getId())
                    .isPresent())
                    .isTrue();
        }

        @Test
        @DisplayName("게시글 좋아요 요청 - 기존에 좋아요가 있는 경우")
        void likePostAlreadyLike() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            PostLike postLike = PostLike.builder()
                    .member(member)
                    .post(post)
                    .build();
            postLikeRepository.save(postLike);

            MultiValueMap<String, String> params = convert(Map.of("isLike", "true"));

            // when
            mockMvc.perform(post("/api/posts/{postId}/like", post.getId())
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            assertThat(postLikeRepository.findByMemberIdAndPostId(member.getId(), post.getId())
                    .isPresent())
                    .isTrue();
        }

        @Test
        @DisplayName("게시글 좋아요 해제 요청 - 기존에 좋아요가 있는 경우")
        void unlikePost() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);
            PostLike postLike = PostLike.builder()
                    .member(member)
                    .post(post)
                    .build();
            postLikeRepository.save(postLike);

            MultiValueMap<String, String> params = convert(Map.of("isLike", "false"));

            // when
            mockMvc.perform(post("/api/posts/{postId}/like", post.getId())
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            assertThat(postLikeRepository.findByMemberIdAndPostId(member.getId(), post.getId())
                    .isPresent())
                    .isFalse();
        }

        @Test
        @DisplayName("게시글 좋아요 해제 요청 - 기존에 좋아요가 없는 경우")
        void unlikePostAlreadyUnlike() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            MultiValueMap<String, String> params = convert(Map.of("isLike", "false"));

            // when
            mockMvc.perform(post("/api/posts/{postId}/like", post.getId())
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            assertThat(postLikeRepository.findByMemberIdAndPostId(member.getId(), post.getId())
                    .isPresent())
                    .isFalse();
        }

        @Test
        @DisplayName("게스트가 게시글 좋아요 요청")
        void likePostByGuest() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            loginGuest();

            MultiValueMap<String, String> params = convert(Map.of("isLike", "false"));

            // expected
            mockMvc.perform(post("/api/posts/{postId}/like", post.getId())
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + guestAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }
}