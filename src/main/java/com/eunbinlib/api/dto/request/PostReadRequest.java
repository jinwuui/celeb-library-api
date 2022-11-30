package com.eunbinlib.api.dto.request;

import lombok.*;

import static java.lang.Math.*;

@Getter
@Setter
public class PostReadRequest {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private final Long after;
    private final Integer size;

    @Builder
    public PostReadRequest(Long after, Integer size) {
        this.after = after;
        this.size = size != null ? size : DEFAULT_SIZE;
    }

    public long getLimit() {
        return min(size, MAX_SIZE);
    }
}
