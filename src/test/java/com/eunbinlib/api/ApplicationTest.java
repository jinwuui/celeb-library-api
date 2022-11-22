package com.eunbinlib.api;

import com.eunbinlib.api.domain.repository.comment.CommentRepository;
import com.eunbinlib.api.domain.repository.post.PostRepository;
import com.eunbinlib.api.domain.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.domain.repository.user.MemberRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_CLASS)
public class ApplicationTest {

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

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
        memberRepository.deleteAll();
        postRepository.deleteAll();
        postImageFileRepository.deleteAll();
        commentRepository.deleteAll();
    }
}
