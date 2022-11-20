package com.eunbinlib.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaginationMeta {
    private final Integer size;
    private final Boolean hasMore;

    @Builder
    private PaginationMeta(Integer size, Boolean hasMore) {
        this.size = size;
        this.hasMore = hasMore;
    }
}
