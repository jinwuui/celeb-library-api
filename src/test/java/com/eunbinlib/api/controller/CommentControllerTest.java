package com.eunbinlib.api.controller;

import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.CommentCreateRequest;
import com.eunbinlib.api.dto.request.CommentUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Objects;

import static com.eunbinlib.api.auth.data.AuthProperties.HEADER_AUTHORIZATION;
import static com.eunbinlib.api.auth.data.AuthProperties.TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class CommentControllerTest extends ControllerTest {

    @Nested
    @DisplayName("create")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class Create {

        @Test
        @DisplayName("댓글 작성")
        void createComment() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            CommentCreateRequest request = new CommentCreateRequest("댓글 내용", post.getId(), null);

            String json = objectMapper.writeValueAsString(request);

            // when & expected
            mockMvc.perform(post("/api/comments")
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isCreated())
                    .andDo(print());

            // then
            Comment comment = commentRepository.findAll().get(0);
            assertThat(comment.getContent())
                    .isEqualTo(request.getContent());
            assertThat(comment.getPost().getId())
                    .isEqualTo(post.getId());
            assertThat(comment.getMember().getId())
                    .isEqualTo(member.getId());
        }

        @Test
        @DisplayName("대댓글을 작성하는 경우")
        void createCommentWithMention() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            CommentCreateRequest request = new CommentCreateRequest("댓글 내용", post.getId(), comment.getId());

            String json = objectMapper.writeValueAsString(request);

            // when & expected
            mockMvc.perform(post("/api/comments")
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
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
                                    Objects.equals(e.getParent().getId(), comment.getId()))
            ).isTrue();
        }

        @Test
        @DisplayName("게스트가 댓글을 다는 경우")
        void createCommentByGuest() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);

            CommentCreateRequest request = new CommentCreateRequest("댓글 내용", post.getId(), null);

            String json = objectMapper.writeValueAsString(request);

            // expected
            loginGuest();

            mockMvc.perform(multipart(HttpMethod.POST, "/api/comments")
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + guestAccessToken)
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
            loginMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용");

            String json = objectMapper.writeValueAsString(request);

            // when & expected
            mockMvc.perform(patch("/api/comments/{commentId}", comment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            Comment findComment = commentRepository.findAll().get(0);
            assertThat(findComment.getContent())
                    .isEqualTo(request.getContent());
            assertThat(findComment.getPost().getId())
                    .isEqualTo(post.getId());
            assertThat(findComment.getMember().getId())
                    .isEqualTo(member.getId());
        }

        @Test
        @DisplayName("다른 사람의 댓글을 수정하는 경우")
        void updateCommentOfAnotherUser() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            Member member2 = getMember();

            String memberAccessToken2 = jwtUtils.createAccessToken(member2.getUserType(), member2.getUsername());
            String memberRefreshToken2 = jwtUtils.createRefreshToken(member2.getUserType(), member2.getUsername());

            userContextRepository.saveUserInfo(memberAccessToken2, memberRefreshToken2, member2);

            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용");

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(patch("/api/comments/{commentId}", comment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken2)
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }

        @Test
        @DisplayName("게스트가 댓글을 수정하는 경우")
        void updateCommentByGuest() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용");

            String json = objectMapper.writeValueAsString(request);

            // expected
            loginGuest();

            mockMvc.perform(patch("/api/comments/{commentId}", comment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + guestAccessToken)
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
            // given
            loginMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            // expected
            mockMvc.perform(delete("/api/comments/{commentId}", comment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 사람의 댓글을 삭제하는 경우")
        void deleteCommentOfAnotherUser() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);


            Member member2 = getMember();

            String memberAccessToken2 = jwtUtils.createAccessToken(member2.getUserType(), member2.getUsername());
            String memberRefreshToken2 = jwtUtils.createRefreshToken(member2.getUserType(), member2.getUsername());

            userContextRepository.saveUserInfo(memberAccessToken2, memberRefreshToken2, member2);


            // expected
            mockMvc.perform(delete("/api/comments/{commentId}", comment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + memberAccessToken2)
                            .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }

        @Test
        @DisplayName("게스트가 댓글을 삭제하는 경우")
        void deleteCommentByGuest() throws Exception {
            // given
            loginMember();
            Post post = getPost(member);
            Comment comment = getComment(member, post);

            // expected
            loginGuest();

            mockMvc.perform(delete("/api/comments/{commentId}", comment.getId())
                            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + guestAccessToken)
                            .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }
}
