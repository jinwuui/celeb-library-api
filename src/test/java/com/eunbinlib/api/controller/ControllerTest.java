package com.eunbinlib.api.controller;

import com.eunbinlib.api.DatabaseCleaner;
import com.eunbinlib.api.application.domain.comment.Comment;
import com.eunbinlib.api.application.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.application.domain.imagefile.PostImageFile;
import com.eunbinlib.api.application.domain.post.Post;
import com.eunbinlib.api.application.domain.repository.block.BlockRepository;
import com.eunbinlib.api.application.domain.repository.comment.CommentRepository;
import com.eunbinlib.api.application.domain.repository.post.PostRepository;
import com.eunbinlib.api.application.domain.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.application.domain.repository.postlike.PostLikeRepository;
import com.eunbinlib.api.application.domain.repository.user.MemberRepository;
import com.eunbinlib.api.application.domain.repository.user.UserRepository;
import com.eunbinlib.api.application.domain.user.Guest;
import com.eunbinlib.api.application.domain.user.Member;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class ControllerTest {

    private static Integer SEQ = 0;

    protected String username = "username";
    protected String password = "password";
    protected String nickname = "nickname";

    protected Member member;
    protected String memberAccessToken;
    protected String memberRefreshToken;

    protected Guest guest;
    protected String guestAccessToken;
    protected String guestRefreshToken;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtUtils jwtUtils;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected BlockRepository blockRepository;
    @Autowired
    protected PostRepository postRepository;
    @Autowired
    protected PostLikeRepository postLikeRepository;
    @Autowired
    protected PostImageFileRepository postImageFileRepository;
    @Autowired
    protected CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        databaseCleaner.afterPropertiesSet();
        databaseCleaner.execute();
    }

    protected void loginMember() {
        member = getMember();
        memberAccessToken = jwtUtils.createAccessToken(member.getUserType(), member.getUsername());
        memberRefreshToken = jwtUtils.createRefreshToken(member.getUserType(), member.getUsername());
    }

    protected void loginGuest() {
        guest = getGuest();
        guestAccessToken = jwtUtils.createAccessToken(guest.getUserType(), guest.getUsername());
        guestRefreshToken = jwtUtils.createRefreshToken(guest.getUserType(), guest.getUsername());
    }

    protected Member getMember() {
        ++SEQ;
        return userRepository.save(Member.builder()
                .username(username + SEQ)
                .password(password + SEQ)
                .nickname(nickname + SEQ)
                .build());
    }

    protected Guest getGuest() {
        ++SEQ;
        return userRepository.save(Guest.builder()
                .username(username + SEQ)
                .password(password + SEQ)
                .build());
    }

    protected Post getPost(Member member) {
        ++SEQ;
        return postRepository.save(Post.builder()
                .title("제목" + SEQ)
                .content("내용" + SEQ)
                .member(member)
                .build());
    }

    protected Comment getComment(Member member, Post post) {
        ++SEQ;
        return commentRepository.save(Comment.builder()
                .content("댓글 내용" + SEQ)
                .member(member)
                .post(post)
                .build());
    }

    protected void addPostImageFile(Post post) {
        ++SEQ;
        BaseImageFile baseImageFile = BaseImageFile.builder()
                .originalFilename(SEQ + "original.jpg")
                .storedFilename(SEQ + "stored.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .byteSize(10L)
                .build();

        postImageFileRepository.save(PostImageFile.builder()
                .baseImageFile(baseImageFile)
                .post(post)
                .build());
    }
}
