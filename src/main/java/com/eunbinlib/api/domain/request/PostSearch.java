package com.eunbinlib.api.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.*;

@Getter
@Setter
public class PostSearch {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 1000;

    private final Integer page;
    private final Integer size;

    @Builder
    public PostSearch(Integer page, Integer size) {
        this.page = page != null ? page : DEFAULT_PAGE;
        this.size = size != null ? size : DEFAULT_SIZE;
    }

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }

    public long getLimit() {
        return min(size, MAX_SIZE);
    }

}
