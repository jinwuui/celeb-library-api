package com.eunbinlib.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PaginationResponse<T> {
    private final PaginationMeta meta;
    private final List<T> data;

    @Builder
    public PaginationResponse(PaginationMeta meta, List<T> data) {
        this.meta = meta;
        this.data = data;
    }
}

