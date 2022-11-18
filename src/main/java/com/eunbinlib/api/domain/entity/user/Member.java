package com.eunbinlib.api.domain.entity.user;


import com.eunbinlib.api.domain.entity.imagefile.ProfileImageFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("MEMBER")
public class Member extends User {

    private String nickname;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "PROFILE_IMAGE_ID")
    private ProfileImageFile profileImage;

    @Builder
    public Member(String username, String password, String nickname) {
        super(username, password);
        this.nickname = nickname;
    }
}
