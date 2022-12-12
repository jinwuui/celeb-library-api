package com.eunbinlib.api.application.domain.repository.postimagefile;

import com.eunbinlib.api.application.domain.imagefile.PostImageFile;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.eunbinlib.api.application.domain.imagefile.QPostImageFile.postImageFile;


@RequiredArgsConstructor
public class PostImageFileRepositoryImpl implements PostImageFileRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PostImageFile> getList(Long limit, Long afterCond) {

        return jpaQueryFactory.selectFrom(postImageFile)
                .limit(limit)
                .where(afterLt(afterCond))
                .orderBy(postImageFile.id.desc())
                .fetch();
    }

    private BooleanExpression afterLt(Long afterCond) {
        return afterCond != null ? postImageFile.id.lt(afterCond) : null;
    }

    @Override
    public boolean existsNext(Long id) {
        return jpaQueryFactory.selectOne()
                .from(postImageFile)
                .where(postImageFile.id.lt(id))
                .fetchFirst() != null;
    }
}
