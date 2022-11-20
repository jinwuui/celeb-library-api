package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.usercontext.UserContextRepository;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.imagefile.PostImageFile;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.PostUpdateRequest;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.eunbinlib.api.dto.request.PostCreateRequest;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.util.MultiValueMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.eunbinlib.api.auth.data.JwtProperties.HEADER_STRING;
import static com.eunbinlib.api.auth.data.JwtProperties.TOKEN_PREFIX;
import static com.eunbinlib.api.dto.request.PostReadRequest.MAX_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtils jwtUtils;

    Member mockMember;
    String mockMemberAccessToken;
    String mockMemberRefreshToken;

    Guest mockGuest;
    String mockGuestAccessToken;
    String mockGuestRefreshToken;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostImageFileRepository postImageFileRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserContextRepository userContextRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void clean() {

        mockMember = Member.builder()
                .username("mockMember")
                .nickname("mockMember")
                .password("mockPassword")
                .build();

        mockMemberAccessToken = jwtUtils.createAccessToken(mockMember.getUserType(), mockMember.getUsername());
        mockMemberRefreshToken = jwtUtils.createRefreshToken(mockMember.getUserType(), mockMember.getUsername());

        mockGuest = Guest.builder()
                .username("mockGuest")
                .password("mockPassword")
                .build();

        mockGuestAccessToken = jwtUtils.createAccessToken(mockGuest.getUserType(), mockGuest.getUsername());
        mockGuestRefreshToken = jwtUtils.createRefreshToken(mockGuest.getUserType(), mockGuest.getUsername());


        userRepository.save(mockMember);
        userRepository.save(mockGuest);

        userContextRepository.saveUserInfo(mockMemberAccessToken, mockMemberRefreshToken, mockMember);
        userContextRepository.saveUserInfo(mockGuestAccessToken, mockGuestRefreshToken, mockGuest);


        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 등록 - 이미지 없음")
    void writeNoImages() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목")
                .content("내용")
                .build();

        // when & expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
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
        List<MultipartFile> images = List.of(
                new MockMultipartFile("images", "test1.png", MediaType.IMAGE_PNG_VALUE, "<<png data>>".getBytes()),
                new MockMultipartFile("images", "test2.jpg", MediaType.IMAGE_PNG_VALUE, "<<jpg data>>".getBytes())
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
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
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
                .isEqualTo(2L);
    }

    @Test
    @DisplayName("글 등록 실패 - 제목 필수 입력")
    void writeTitleNotBlank() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .content("내용")
                .build();

        // expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
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
        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목")
                .build();

        // expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
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
        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목")
                .content("내용")
                .build();

        // expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(HEADER_STRING, TOKEN_PREFIX + mockGuestAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 조회")
    void readOne() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);

        postRepository.save(post);

        // expected
        mockMvc.perform(get("/api/posts/{postId}", post.getId())
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void readMany() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(0, 20)
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

        MultiValueMap<String, String> params = MultiValueMapper.convert(objectMapper, postReadRequest);

        // expected
        mockMvc.perform(get("/api/posts")
                        .params(params)
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..['data'].length()", is(5)))
                .andExpect(jsonPath("$..['data'][0].id").value(20))
                .andExpect(jsonPath("$..['data'][0].title").value("제목19"))
                .andExpect(jsonPath("$..['data'][0].content").value("내용19"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 여러개 조회 - page가 0이하, size가 10000")
    void readManyEdgeCase() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(0, 2000)
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
                .size(10000)
                .build();

        MultiValueMap<String, String> params = MultiValueMapper.convert(objectMapper, postReadRequest);

        // expected
        mockMvc.perform(get("/api/posts")
                        .params(params)
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..['data'].length()", is(MAX_SIZE)))
                .andExpect(jsonPath("$..['data'][0].id").value(2000))
                .andExpect(jsonPath("$..['data'][0].title").value("제목1999"))
                .andExpect(jsonPath("$..['data'][0].content").value("내용1999"));
    }

    @Test
    @DisplayName("글 제목 수정")
    void editTitle() throws Exception {
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

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", post.getId())
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateRequest))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 내용 수정")
    void editContent() throws Exception {
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

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", post.getId())
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
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
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);
        postRepository.save(post);

        // expected
        mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void readPostNotFound() throws Exception {
        // expected
        mockMvc.perform(get("/api/posts/{postId}", 1L)
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void editPostNotFound() throws Exception {
        // given
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("제목")
                .content("수정된내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", 1L)
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
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
        mockMvc.perform(delete("/api/posts/{postId}", 1L)
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("다른 사람의 글을 삭제하는 경우")
    void deletePostOfAnotherUser() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);
        postRepository.save(post);


        Member mockMember2 = Member.builder()
                .username("mockMember2")
                .nickname("mockMember2")
                .password("mockPassword2")
                .build();

        String mockMemberAccessToken2 = jwtUtils.createAccessToken(mockMember2.getUserType(), mockMember2.getUsername());
        String mockMemberRefreshToken2 = jwtUtils.createRefreshToken(mockMember2.getUserType(), mockMember2.getUsername());

        userRepository.save(mockMember2);

        userContextRepository.saveUserInfo(mockMemberAccessToken2, mockMemberRefreshToken2, mockMember2);


        // expected
        mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                        .header(HEADER_STRING, TOKEN_PREFIX + mockMemberAccessToken2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

}