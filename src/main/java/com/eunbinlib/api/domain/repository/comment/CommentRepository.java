package com.eunbinlib.api.domain.repository.comment;

import com.eunbinlib.api.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
