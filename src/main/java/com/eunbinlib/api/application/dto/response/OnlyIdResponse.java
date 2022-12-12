package com.eunbinlib.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OnlyIdResponse {
    final Long id;

    @Builder
    public OnlyIdResponse(Long id) {
        this.id = id;
    }

    public static OnlyIdResponse from(Long id) {
        return OnlyIdResponse.builder()
                .id(id)
                .build();
    }
}
