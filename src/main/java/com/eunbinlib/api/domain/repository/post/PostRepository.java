package com.eunbinlib.api.domain.repository.post;

import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Optional<Post> findByIdAndStateNot(Long id, PostState state);

}
