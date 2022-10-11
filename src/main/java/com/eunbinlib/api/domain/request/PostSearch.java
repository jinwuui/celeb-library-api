package com.eunbinlib.api.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.*;

@Getter
@Setter
public class PostSearch {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private final Long after;
    private final Integer size;

    @Builder
    public PostSearch(Long after, Integer size) {
        this.after = after;
        this.size = size != null ? size : DEFAULT_SIZE;
    }

    public long getOffset() {
        return 0L;
//        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }

    public long getLimit() {
        return min(size, MAX_SIZE);
    }

}
