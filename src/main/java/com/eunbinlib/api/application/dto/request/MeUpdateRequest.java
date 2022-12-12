package com.eunbinlib.api.application.dto.request;

import com.eunbinlib.api.application.domain.user.Nickname;
import com.eunbinlib.api.application.dto.validannotation.AllFieldsIsNullOrNot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AllFieldsIsNullOrNot(fields = {"nickname", "profileImageFile"})
public class MeUpdateRequest {

    @Size(min = Nickname.MIN_NICKNAME_LENGTH, max = Nickname.MAX_NICKNAME_LENGTH, message = "{min} ~ {max} 글자의 닉네임을 입력해주세요. 현재 닉네임: ${validatedValue}")
    @Pattern(regexp = Nickname.NICKNAME_REGEXP, message = "닉네임은 한글, 영어, 숫자, 공백만 포함 가능합니다. 현재 닉네임: ${validatedValue}")
    private String nickname;

    private MultipartFile profileImageFile;
}
