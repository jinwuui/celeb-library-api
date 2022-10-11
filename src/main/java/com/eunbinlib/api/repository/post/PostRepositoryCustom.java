package com.eunbinlib.api.repository.post;

import com.eunbinlib.api.domain.entity.post.Post;
import com.eunbinlib.api.domain.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);

    boolean existsNext(Long id);

}
