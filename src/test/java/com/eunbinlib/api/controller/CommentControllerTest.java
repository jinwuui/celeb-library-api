package com.eunbinlib.api.controller;

import com.eunbinlib.api.auth.usercontext.UserContextRepository;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.repository.comment.CommentRepository;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.CommentCreateRequest;
import com.eunbinlib.api.dto.request.CommentUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static com.eunbinlib.api.auth.data.JwtProperties.HEADER_AUTHORIZATION;
import static com.eunbinlib.api.auth.data.JwtProperties.TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostImageFileRepository postImageFileRepository;
    @Autowired
    UserContextRepository userContextRepository;

    @Autowired
    ObjectMapper objectMapper;

    Member mockMember;
    String mockMemberAccessToken;
    String mockMemberRefreshToken;

    Member mockMember2;
    String mockMember2AccessToken;
    String mockMember2RefreshToken;

    Guest mockGuest;
    String mockGuestAccessToken;
    String mockGuestRefreshToken;

    Post mockPost;

    Comment mockComment;

    @BeforeEach
    void beforeEach() {

        mockMember = Member.builder()
                .username("mockMember")
                .nickname("mockMember")
                .password("mockPassword")
                .build();

        mockMemberAccessToken = jwtUtils.createAccessToken(mockMember.getUserType(), mockMember.getUsername());
        mockMemberRefreshToken = jwtUtils.createRefreshToken(mockMember.getUserType(), mockMember.getUsername());

        mockMember2 = Member.builder()
                .username("mockMember2")
                .nickname("mockMember2")
                .password("mockPassword2")
                .build();

        mockMember2AccessToken = jwtUtils.createAccessToken(mockMember2.getUserType(), mockMember2.getUsername());
        mockMember2RefreshToken = jwtUtils.createRefreshToken(mockMember2.getUserType(), mockMember2.getUsername());

        mockGuest = Guest.builder()
                .username("mockGuest")
                .password("mockPassword")
                .build();

        mockGuestAccessToken = jwtUtils.createAccessToken(mockGuest.getUserType(), mockGuest.getUsername());
        mockGuestRefreshToken = jwtUtils.createRefreshToken(mockGuest.getUserType(), mockGuest.getUsername());

        userRepository.save(mockMember);
        userRepository.save(mockMember2);
        userRepository.save(mockGuest);

        userContextRepository.saveUserInfo(mockMemberAccessToken, mockMemberRefreshToken, mockMember);
        userContextRepository.saveUserInfo(mockMember2AccessToken, mockMember2RefreshToken, mockMember2);
        userContextRepository.saveUserInfo(mockGuestAccessToken, mockGuestRefreshToken, mockGuest);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .state(PostState.NORMAL)
                .build();
        post.setMember(mockMember);

        mockPost = postRepository.save(post);


        Comment comment = Comment.builder()
                .content("댓글내용")
                .member(mockMember)
                .post(mockPost)
                .build();

        mockComment = commentRepository.save(comment);
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("create")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class Create {

        @Test
        @DisplayName("댓글 작성")
        void createComment() throws Exception {
            // given
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .content("댓글내용")
                    .postId(mockPost.getId())
                    .build();

            String json = objectMapper.writeValueAsString(request);

            // when & expected
            mockMvc.perform(post("/api/comments")
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + mockMemberAccessToken)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isCreated())
                    .andDo(print());

            // then
            Comment comment = commentRepository.findAll().get(0);
            assertThat(comment.getContent())
                    .isEqualTo("댓글내용");
            assertThat(comment.getPost().getId())
                    .isEqualTo(mockPost.getId());
            assertThat(comment.getMember().getId())
                    .isEqualTo(mockMember.getId());
        }

        @Test
        @DisplayName("대댓글을 작성하는 경우")
        void createCommentWithMention() throws Exception {
            // given
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .content("댓글내용")
                    .postId(mockPost.getId())
                    .parentId(mockComment.getId())
                    .build();

            String json = objectMapper.writeValueAsString(request);

            // when & expected
            mockMvc.perform(post("/api/comments")
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + mockMemberAccessToken)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn();

            // then
            assertThat(commentRepository.findAll()
                    .stream()
                    .anyMatch((e) ->
                            e.getParent() != null &&
                                    Objects.equals(e.getParent().getId(), mockComment.getId()))
            ).isTrue();
        }

        @Test
        @DisplayName("게스트가 댓글을 다는 경우")
        void createCommentByGuest() throws Exception {
            // given
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .content("댓글내용")
                    .postId(mockPost.getId())
                    .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(multipart(HttpMethod.POST, "/api/comments")
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + mockGuestAccessToken)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("update")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class Update {

        @Test
        @DisplayName("댓글을 수정하는 경우")
        void updateComment() throws Exception {
            // given
            CommentUpdateRequest request = CommentUpdateRequest.builder()
                    .content("수정된 댓글 내용")
                    .build();

            String json = objectMapper.writeValueAsString(request);

            // when & expected
            mockMvc.perform(patch("/api/comments/{commentId}", mockComment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + mockMemberAccessToken)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            Comment comment = commentRepository.findAll().get(0);
            assertThat(comment.getContent())
                    .isEqualTo(request.getContent());
            assertThat(comment.getPost().getId())
                    .isEqualTo(mockPost.getId());
            assertThat(comment.getMember().getId())
                    .isEqualTo(mockMember.getId());
        }

        @Test
        @DisplayName("다른 사람의 댓글을 수정하는 경우")
        void updateCommentOfAnotherUser() throws Exception {
            // given
            CommentUpdateRequest request = CommentUpdateRequest.builder()
                    .content("수정된 댓글 내용")
                    .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(patch("/api/comments/{commentId}", mockComment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + mockMember2AccessToken)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }

        @Test
        @DisplayName("게스트가 댓글을 다는 경우")
        void updateCommentByGuest() throws Exception {
            // given
            CommentUpdateRequest request = CommentUpdateRequest.builder()
                    .content("댓글내용")
                    .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(patch("/api/comments/{commentId}", mockComment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + mockGuestAccessToken)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("delete")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class Delete {

        @Test
        @DisplayName("댓글을 삭제하는 경우")
        void deleteComment() throws Exception {
            // expected
            mockMvc.perform(delete("/api/comments/{commentId}", mockComment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + mockMemberAccessToken)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 사람의 댓글을 삭제하는 경우")
        void deleteCommentOfAnotherUser() throws Exception {
            // expected
            mockMvc.perform(delete("/api/comments/{commentId}", mockComment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + mockMember2AccessToken)
                            .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }

        @Test
        @DisplayName("게스트가 댓글을 삭제하는 경우")
        void deleteCommentByGuest() throws Exception {
            // expected
            mockMvc.perform(delete("/api/comments/{commentId}", mockComment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + mockGuestAccessToken)
                            .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }
}
