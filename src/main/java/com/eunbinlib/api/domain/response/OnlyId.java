package com.eunbinlib.api.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OnlyId {
    final Long id;

    @Builder
    public OnlyId(Long id) {
        this.id = id;
    }
}
