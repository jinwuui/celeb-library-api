package com.eunbinlib.api.domain.user;

import com.eunbinlib.api.application.domain.user.Guest;
import com.eunbinlib.api.application.domain.user.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    @DisplayName("회원의 사용자 타입을 구하는 경우")
    void getUserTypeOfMember() {
        // given
        Member member = Member.builder()
                .nickname("닉네임")
                .build();

        // then
        Assertions.assertThat(member.getUserType())
                .isEqualTo("member");
    }


    @Test
    @DisplayName("게스트의 사용자 타입을 구하는 경우")
    void getUserTypeOfGuest() {
        // given
        Guest guest = Guest.builder().build();

        // then
        Assertions.assertThat(guest.getUserType())
                .isEqualTo("guest");
    }
}
