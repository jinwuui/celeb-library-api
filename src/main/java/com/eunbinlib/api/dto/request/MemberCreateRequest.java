package com.eunbinlib.api.dto.request;

import com.eunbinlib.api.domain.user.Nickname;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class MemberCreateRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private final String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String password;

    @NotBlank
    @Size(min = Nickname.MIN_NICKNAME_LENGTH, max = Nickname.MAX_NICKNAME_LENGTH, message = "{min} ~ {max} 글자의 닉네임을 입력해주세요. 현재 닉네임: ${validatedValue}")
    @Pattern(regexp = Nickname.NICKNAME_REGEXP, message = "닉네임은 한글, 영어, 숫자, 공백만 포함 가능합니다. 현재 닉네임: ${validatedValue}")
    private final String nickname;

    @Builder
    public MemberCreateRequest(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
