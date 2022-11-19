package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.data.JwtProperties;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.entity.imagefile.PostImageFile;
import com.eunbinlib.api.domain.entity.post.Post;
import com.eunbinlib.api.domain.entity.post.PostState;
import com.eunbinlib.api.domain.request.PostEdit;
import com.eunbinlib.api.domain.request.PostSearch;
import com.eunbinlib.api.domain.request.PostWrite;
import com.eunbinlib.api.repository.post.PostRepository;
import com.eunbinlib.api.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.util.MultiValueMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.eunbinlib.api.domain.request.PostSearch.MAX_SIZE;
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

    String token;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostImageFileRepository postImageFileRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void clean() {
        token = JwtProperties.TOKEN_PREFIX + jwtUtils.createAccessToken("testuser");
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 등록 - 이미지 없음")
    void writeNoImages() throws Exception {
        // given
        PostWrite request = PostWrite.builder()
                .title("제목")
                .content("내용")
                .build();

        // when & expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(JwtProperties.HEADER_STRING, token)
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

        PostWrite request = PostWrite.builder()
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
                        .header(JwtProperties.HEADER_STRING, token)
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
        PostWrite request = PostWrite.builder()
                .content("내용")
                .build();

        // expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(JwtProperties.HEADER_STRING, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("글 등록 실패 - 내용 필수 입력")
    void writeContentNotBlank() throws Exception {
        // given
        PostWrite request = PostWrite.builder()
                .title("제목")
                .build();

        // expected
        mockMvc.perform(multipart(HttpMethod.POST, "/api/posts")
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .header(JwtProperties.HEADER_STRING, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요."))
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

        postRepository.save(post);

        // expected
        mockMvc.perform(get("/api/posts/{postId}", post.getId())
                        .header(JwtProperties.HEADER_STRING, token)
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
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .state(PostState.NORMAL)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .after(null)
                .size(5)
                .build();

        MultiValueMap<String, String> params = MultiValueMapper.convert(objectMapper, postSearch);

        // expected
        mockMvc.perform(get("/api/posts")
                        .params(params)
                        .header(JwtProperties.HEADER_STRING, token)
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
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .state(PostState.NORMAL)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .after(null)
                .size(10000)
                .build();

        MultiValueMap<String, String> params = MultiValueMapper.convert(objectMapper, postSearch);

        // expected
        mockMvc.perform(get("/api/posts")
                        .params(params)
                        .header(JwtProperties.HEADER_STRING, token)
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
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("수정된제목")
                .content("내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", post.getId())
                        .header(JwtProperties.HEADER_STRING, token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
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
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목")
                .content("수정된내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", post.getId())
                        .header(JwtProperties.HEADER_STRING, token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
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
        postRepository.save(post);

        // expected
        mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                        .header(JwtProperties.HEADER_STRING, token)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void readPostNotFound() throws Exception {
        // expected
        mockMvc.perform(get("/api/posts/{postId}", 1L)
                        .header(JwtProperties.HEADER_STRING, token)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void editPostNotFound() throws Exception {
        // given
        PostEdit postEdit = PostEdit.builder()
                .title("제목")
                .content("수정된내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", 1L)
                        .header(JwtProperties.HEADER_STRING, token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제")
    void deletePostNotFound() throws Exception {
        // expected
        mockMvc.perform(delete("/api/posts/{postId}", 1L)
                        .header(JwtProperties.HEADER_STRING, token)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}