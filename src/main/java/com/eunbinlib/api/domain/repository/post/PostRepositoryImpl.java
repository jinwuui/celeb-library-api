package com.eunbinlib.api.domain.repository.post;

import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.domain.post.PostState;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.eunbinlib.api.domain.post.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(Long limit, Long afterCond) {

        return jpaQueryFactory.selectFrom(post)
                .limit(limit)
                .where(
                        stateNe(PostState.DELETED),
                        afterLt(afterCond)
                )
                .orderBy(post.id.desc())
                .fetch();
    }

    @Override
    public boolean existsNext(Long afterCond) {
        return jpaQueryFactory.selectOne()
                .from(post)
                .where(
                        stateNe(PostState.DELETED),
                        afterLt(afterCond)
                )
                .fetchFirst() != null;
    }

    private BooleanExpression stateNe(PostState stateCond) {
        return post.state.ne(stateCond);
    }

    private BooleanExpression afterLt(Long afterCond) {
        return afterCond != null ? post.id.lt(afterCond) : null;
    }
}
