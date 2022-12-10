package com.eunbinlib.api.domain.user;

import com.eunbinlib.api.exception.type.application.EunbinlibIllegalArgumentException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor
@Embeddable
public class Nickname {

    public static final int MIN_NICKNAME_LENGTH = 2;
    public static final int MAX_NICKNAME_LENGTH = 13;

    public static final String NICKNAME_REGEXP = "[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\\s]+";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEXP);

    @Column(name = "nickname", unique = true)
    private String value;

    public Nickname(String value) {
        validateNull(value);
        value = value.trim();
        validateNickname(value);
        this.value = value;
    }

    private void validateNull(String value) {
        if (value == null) {
            throw new EunbinlibIllegalArgumentException("nickname", "null 값이 입력되었습니다.");
        }
    }

    private void validateNickname(final String value) {
        if (value.length() < MIN_NICKNAME_LENGTH || value.length() > MAX_NICKNAME_LENGTH) {
            throw new EunbinlibIllegalArgumentException(
                    "nickname",
                    String.format("%d ~ %d 글자를 입력해주세요. 현재 닉네임 길이: %d", MIN_NICKNAME_LENGTH, MAX_NICKNAME_LENGTH, value.length())
            );
        }
        if (!NICKNAME_PATTERN.matcher(value).matches()) {
            throw new EunbinlibIllegalArgumentException(
                    "nickname",
                    String.format("한글, 영어, 숫자, 공백만 포함 가능합니다. 현재 닉네임: %s", value));
        }
    }
}
