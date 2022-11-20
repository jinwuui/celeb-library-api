package com.eunbinlib.api.domain.repository.post;

import com.eunbinlib.api.domain.post.Post;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(Long limit, Long afterCond);

    boolean existsNext(Long id);

}
