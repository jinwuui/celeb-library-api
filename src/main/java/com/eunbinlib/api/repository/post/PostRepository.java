package com.eunbinlib.api.repository.post;

import com.eunbinlib.api.domain.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}
