package com.eunbinlib.api.domain.entity.user;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("MEMBER")
public class Member extends User {

    private String nickname;

    private String imageUrl;

    @Builder
    public Member(String username, String password, String nickname, String imageUrl) {
        super(username, password);
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }
}
