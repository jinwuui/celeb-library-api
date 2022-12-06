package com.eunbinlib.api.domain.repository.post;

import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    String FIND_POSTS_QUERY_BASE = "select *\n" +
                                   "from post p\n" +
                                   "    left join (select blocked_id from block where blocker_id = :userId) b on p.member_id = b.blocked_id\n" +
                                   "where b.blocked_id is null\n" +
                                   "    and p.state = 'NORMAL'\n";

    String FIND_POSTS_QUERY_AFTER_CONDITION = "    and p.id < :afterCond\n";

    String FIND_POSTS_QUERY_TAIL = "order by p.id desc\n" +
                                   "limit :limit\n";

    Optional<Post> findByIdAndStateNot(Long id, PostState state);

    @Query(value = FIND_POSTS_QUERY_BASE +
            FIND_POSTS_QUERY_TAIL, nativeQuery = true)
    List<Post> findPosts(@Param("limit") Long limit, @Param("userId") Long userId);

    @Query(value = FIND_POSTS_QUERY_BASE +
            FIND_POSTS_QUERY_AFTER_CONDITION +
            FIND_POSTS_QUERY_TAIL, nativeQuery = true)
    List<Post> findPostsWithAfterCondition(@Param("limit") Long limit, @Param("afterCond") Long afterCond, @Param("userId") Long userId);

    @Query(value = "select case when count(p.id) = 0 then false else true end\n" +
            "from post p\n" +
            "   left join (select blocked_id from block where blocker_id = :userId) b on p.member_id = b.blocked_id\n" +
            "where b.blocked_id is null\n" +
            "   and p.state = 'NORMAL'\n" +
            "   and p.id < :afterCond\n", nativeQuery = true)
    boolean existsNext(@Param("userId") Long userId, @Param("afterCond") Long afterCond);
}