package com.eunbinlib.api.controller;

import com.eunbinlib.api.ControllerTest;
import com.eunbinlib.api.domain.imagefile.PostImageFile;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.util.MultiValueMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.eunbinlib.api.auth.data.JwtProperties.HEADER_AUTHORIZATION;
import static com.eunbinlib.api.auth.data.JwtProperties.TOKEN_PREFIX;
import static com.eunbinlib.api.dto.request.PostReadRequest.MAX_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest extends ControllerTest {

    @Test
    @DisplayName("글 등록 - 이미지 없음")
    void writeNoImages() throws Exception {
        // given
        loginMember();
        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목")
                .content("내용")
                .build();

        // when & expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
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
    void writeWithImages() throws Exception {
        // given
        loginMember();
        List<MultipartFile> images = List.of(
                new MockMultipartFile("images", "test1.png", MediaType.IMAGE_PNG_VALUE, "<<png data>>".getBytes()),
                new MockMultipartFile("images", "test2.jpg", MediaType.IMAGE_JPEG_VALUE, "<<jpg data>>".getBytes())
        );

        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목")
                .content("내용")
                .images(images)
                .build();

        // when & expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .file((MockMultipartFile) images.get(0))
                        .file((MockMultipartFile) images.get(1))
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
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
    void writeTitleNotBlank() throws Exception {
        // given
        loginMember();
        PostCreateRequest request = PostCreateRequest.builder()
                .content("내용")
                .build();

        // expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
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
    void writeContentNotBlank() throws Exception {
        // given
        loginMember();
        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목")
                .build();

        // expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
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
    void writeByGuest() throws Exception {
        // given
        loginGuest();
        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목")
                .content("내용")
                .build();

        // expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + guestAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 상세 조회")
    void readDetail() throws Exception {
        // given
        loginMember();
        Post post = getPost(member);

        // expected
        mockMvc.perform(get("/api/posts/{postId}", post.getId())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.viewCount").value(1L))
                .andExpect(jsonPath("$.likeCount").value(0L))
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
                                .state(PostState.NORMAL)
                                .member(member)
                                .build()
                )
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostReadRequest postReadRequest = PostReadRequest.builder()
                .after(null)
                .size(5)
                .build();

        MultiValueMap<String, String> params = MultiValueMapper.convert(objectMapper, postReadRequest);

        // expected
        mockMvc.perform(get("/api/posts")
                        .params(params)
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
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
                                .state(PostState.NORMAL)
                                .member(member)
                                .build()
                )
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostReadRequest postReadRequest = PostReadRequest.builder()
                .after(null)
                .size(10000)
                .build();

        MultiValueMap<String, String> params = MultiValueMapper.convert(objectMapper, postReadRequest);

        // expected
        mockMvc.perform(get("/api/posts")
                        .params(params)
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..['data'].length()", is(MAX_SIZE)))
                .andExpect(jsonPath("$..['data'][0].title").value("제목1999"))
                .andExpect(jsonPath("$..['data'][0].content").value("내용1999"));
    }

    @Test
    @DisplayName("글 제목 수정")
    void updateTitle() throws Exception {
        // given
        loginMember();
        Post post = getPost(member);

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("수정된제목")
                .content(post.getContent())
                .build();

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", post.getId())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateRequest))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 내용 수정")
    void updateContent() throws Exception {
        // given
        loginMember();
        Post post = getPost(member);

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title(post.getTitle())
                .content("수정된내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", post.getId())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateRequest))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 삭제")
    void deletePost() throws Exception {
        // given
        loginMember();
        Post post = getPost(member);

        // expected
        mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void readPostNotFound() throws Exception {
        loginMember();

        // expected
        mockMvc.perform(get("/api/posts/{postId}", 1L)
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void updatePostNotFound() throws Exception {
        // given
        loginMember();
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("제목")
                .content("수정된내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", 1L)
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateRequest))
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제")
    void deletePostNotFound() throws Exception {
        // expected
        loginMember();
        mockMvc.perform(delete("/api/posts/{postId}", 1L)
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
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

        userContextRepository.saveUserInfo(memberAccessToken2, memberRefreshToken2, member2);

        // expected
        mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden())
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

        userContextRepository.saveUserInfo(memberAccessToken2, memberRefreshToken2, member2);

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", post.getId())
                        .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken2)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateRequest))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}