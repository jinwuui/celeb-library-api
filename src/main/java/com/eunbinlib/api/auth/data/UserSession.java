package com.eunbinlib.api.auth.data;

import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class UserSession {

    private Long id;

    private String userType;

    private String username;

    public static UserSession from(User user) {
        if (user instanceof Member) {
            return MemberSession.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .userType(user.getUserType())
                    .nickname(((Member) user).getNickname().getValue())
                    .build();
        } else {
            return UserSession.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .userType(user.getUserType())
                    .build();
        }
    }
}
