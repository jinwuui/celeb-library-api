package com.eunbinlib.api.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginReq {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    public LoginReq() {
    }

    @Builder
    public LoginReq(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
