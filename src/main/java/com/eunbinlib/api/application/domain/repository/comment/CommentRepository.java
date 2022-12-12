package com.eunbinlib.api.application.domain.repository.comment;

import com.eunbinlib.api.application.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
