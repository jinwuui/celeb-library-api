package com.eunbinlib.api.domain.repository.postlike;

import com.eunbinlib.api.domain.postlike.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByMemberIdAndPostId(Long memberId, Long postId);

}
