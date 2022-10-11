package com.eunbinlib.api.repository.post;

import com.eunbinlib.api.domain.entity.post.Post;
import com.eunbinlib.api.domain.request.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.eunbinlib.api.domain.entity.post.QPost.post;


@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PostSearch postSearch) {
        if (postSearch.getAfter() == null) {
            // 처음부터 조회
            return jpaQueryFactory.selectFrom(post)
                    .limit(postSearch.getLimit())
                    .orderBy(post.id.desc())
                    .fetch();
        } else {
            // 특정 게시글 이후부터 조회
            return jpaQueryFactory.selectFrom(post)
                    .limit(postSearch.getLimit())
                    .where(post.id.lt(postSearch.getAfter()))
                    .orderBy(post.id.desc())
                    .fetch();
        }

    }

    @Override
    public boolean existsNext(Long id) {
        return jpaQueryFactory.selectOne()
                .from(post)
                .where(post.id.lt(id))
                .fetchFirst() != null;
    }

}
