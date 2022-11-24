package com.eunbinlib.api.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class GuestCreateRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private final String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String password;

    @Builder
    public GuestCreateRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
