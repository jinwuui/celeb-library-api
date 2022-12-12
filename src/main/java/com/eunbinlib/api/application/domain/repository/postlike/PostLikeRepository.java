package com.eunbinlib.api.application.domain.repository.postlike;

import com.eunbinlib.api.application.domain.postlike.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByMemberIdAndPostId(Long memberId, Long postId);

}
