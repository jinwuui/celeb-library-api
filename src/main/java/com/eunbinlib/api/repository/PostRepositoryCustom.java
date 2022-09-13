package com.eunbinlib.api.repository;

import com.eunbinlib.api.domain.entity.Post;
import com.eunbinlib.api.domain.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);

}
