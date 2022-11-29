package com.eunbinlib.api.service;

import com.eunbinlib.api.DatabaseCleaner;
import com.eunbinlib.api.domain.comment.Comment;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.eunbinlib.api.domain.repository.comment.CommentRepository;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.domain.repository.postlike.PostLikeRepository;
import com.eunbinlib.api.domain.repository.user.MemberRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class ServiceTest {

    private static Integer SEQ = 0;

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected PostRepository postRepository;
    @Autowired
    protected PostImageFileRepository postImageFileRepository;
    @Autowired
    protected PostLikeRepository postLikeRepository;
    @Autowired
    protected CommentRepository commentRepository;

    protected String username = "username";
    protected String password = "password";
    protected String nickname = "nickname";

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.afterPropertiesSet();
        databaseCleaner.execute();
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
