package com.eunbinlib.api.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    public LoginRequest() {
    }

    @Builder
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
