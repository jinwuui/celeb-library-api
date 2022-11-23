package com.eunbinlib.api;

import com.eunbinlib.api.auth.usercontext.MapUserContextRepository;
import com.eunbinlib.api.auth.utils.JwtUtils;
import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.repository.comment.CommentRepository;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.domain.repository.user.MemberRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {

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
    protected PostRepository postRepository;
    @Autowired
    protected PostImageFileRepository postImageFileRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected MapUserContextRepository userContextRepository;

    @BeforeEach
    void setUp() {
        databaseCleaner.afterPropertiesSet();
        databaseCleaner.execute();
    }

    protected void loginMember() {
        member = getMember();
        memberAccessToken = jwtUtils.createAccessToken(member.getUserType(), member.getUsername());
        memberRefreshToken = jwtUtils.createRefreshToken(member.getUserType(), member.getUsername());
        userContextRepository.saveUserInfo(memberAccessToken, memberRefreshToken, member);
    }

    protected void loginGuest() {
        guest = getGuest();
        guestAccessToken = jwtUtils.createAccessToken(guest.getUserType(), guest.getUsername());
        guestRefreshToken = jwtUtils.createRefreshToken(guest.getUserType(), guest.getUsername());
        userContextRepository.saveUserInfo(guestAccessToken, guestRefreshToken, guest);
    }

    protected Member getMember() {
        ++SEQ;
        Member member = Member.builder()
                .username(username + SEQ)
                .password(password + SEQ)
                .nickname(nickname + SEQ)
                .build();
        return userRepository.save(member);
    }

    protected Guest getGuest() {
        ++SEQ;
        Guest guest = Guest.builder()
                .username(username + SEQ)
                .password(password + SEQ)
                .build();
        return userRepository.save(guest);
    }

    protected Post getPost(Member member) {
        ++SEQ;
        return postRepository.save(Post.builder()
                .title("제목" + SEQ)
                .content("내용" + SEQ)
                .state(PostState.NORMAL)
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
}
