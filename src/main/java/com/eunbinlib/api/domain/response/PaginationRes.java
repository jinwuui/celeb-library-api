package com.eunbinlib.api.domain.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PaginationRes<T> {
    private final PaginationMeta meta;
    private final List<T> data;

    @Builder
    public PaginationRes(PaginationMeta meta, List<T> data) {
        this.meta = meta;
        this.data = data;
    }

}

