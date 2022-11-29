package com.eunbinlib.api.domain.user;

import com.eunbinlib.api.exception.type.EunbinlibIllegalArgumentException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NicknameTest {

    @Test
    @DisplayName("정상적으로 닉네임이 생성되는 경우")
    void create() {
        Nickname nickname = new Nickname("닉네임");
    }

    @Test
    @DisplayName("닉네임에 null 값이 들어가는 경우")
    void createNullValue() {
        Assertions.assertThatThrownBy(() -> new Nickname(null))
                .isInstanceOf(EunbinlibIllegalArgumentException.class);
    }

    @Test
    @DisplayName("닉네임이 너무 짧은 경우")
    void nicknameIsTooShort() {
        Assertions.assertThatThrownBy(() -> new Nickname("가"))
                .isInstanceOf(EunbinlibIllegalArgumentException.class);
    }

    @Test
    @DisplayName("닉네임에 null 값이 들어가는 경우")
    void nicknameIsTooLong() {
        Assertions.assertThatThrownBy(() -> new Nickname("abcdefghijklmnop"))
                .isInstanceOf(EunbinlibIllegalArgumentException.class);
    }

    @Test
    @DisplayName("닉네임에 혀용되지 않는 문자가 들어가는 경우")
    void nicknameUnacceptableCharacter() {
        Assertions.assertThatThrownBy(() -> new Nickname("@#$%^&*("))
                .isInstanceOf(EunbinlibIllegalArgumentException.class);
    }
}
