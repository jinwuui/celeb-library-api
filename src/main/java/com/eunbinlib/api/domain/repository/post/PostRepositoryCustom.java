package com.eunbinlib.api.domain.repository.post;

import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.dto.request.PostReadRequest;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostReadRequest postReadRequest);

    boolean existsNext(Long id);

}
