package com.eunbinlib.api.utils;

import com.eunbinlib.api.application.utils.EncryptUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.assertj.core.api.Assertions.assertThat;

public class EncryptUtilsTest {

    @Test
    @DisplayName("비밀번호에 BCript 적용")
    void encrypt() {
        // given
        String plainPassword = "abcde1234";
        String otherPassword = "0987zxcv";

        // when
        String hashedPassword = EncryptUtils.encrypt(plainPassword);

        // expected
        assertThat(BCrypt.checkpw(plainPassword, hashedPassword))
                .isTrue();
        assertThat(BCrypt.checkpw(otherPassword, hashedPassword))
                .isFalse();
    }

    @Test
    @DisplayName("비밀번호를 검사하는 경우")
    void checkPassword() {
        // given
        String plainPassword = "abcde1234";
        String otherPassword = "0987zxcv";
        String hashedPassword = EncryptUtils.encrypt(plainPassword);

        // expected
        assertThat(EncryptUtils.isNotMatch(plainPassword, hashedPassword))
                .isFalse();
        assertThat(EncryptUtils.isNotMatch(otherPassword, hashedPassword))
                .isTrue();
    }
}
