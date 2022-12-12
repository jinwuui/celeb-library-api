package com.eunbinlib.api.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.lang.Math.min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageReadRequest {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private Long after;

    private Integer size;

    public Integer getSize() {
        return size != null ? size : DEFAULT_SIZE;
    }

    public long getLimit() {
        return min(getSize(), MAX_SIZE);
    }
}
