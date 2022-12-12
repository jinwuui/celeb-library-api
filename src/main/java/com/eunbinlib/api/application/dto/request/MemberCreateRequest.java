package com.eunbinlib.api.application.dto.request;

import com.eunbinlib.api.application.domain.user.Nickname;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreateRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank
    @Size(min = Nickname.MIN_NICKNAME_LENGTH, max = Nickname.MAX_NICKNAME_LENGTH, message = "{min} ~ {max} 글자의 닉네임을 입력해주세요. 현재 닉네임: ${validatedValue}")
    @Pattern(regexp = Nickname.NICKNAME_REGEXP, message = "닉네임은 한글/영어/숫자/공백만 포함 가능합니다. 현재 닉네임: ${validatedValue}")
    private String nickname;
}
