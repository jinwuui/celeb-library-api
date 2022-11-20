package com.eunbinlib.api.domain.repository.post;

import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.dto.request.PostReadRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.eunbinlib.api.domain.post.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PostReadRequest postReadRequest) {
        if (postReadRequest.getAfter() == null) {
            // 처음부터 조회
            return jpaQueryFactory.selectFrom(post)
                    .limit(postReadRequest.getLimit())
                    .orderBy(post.id.desc())
                    .fetch();
        } else {
            // 특정 게시글 이후부터 조회
            return jpaQueryFactory.selectFrom(post)
                    .limit(postReadRequest.getLimit())
                    .where(post.id.lt(postReadRequest.getAfter()))
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
