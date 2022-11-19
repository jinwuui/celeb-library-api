package com.eunbinlib.api.domain.entity.imagefile;

import com.eunbinlib.api.domain.entity.user.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImageFile extends BaseImageFile {

    @OneToOne(mappedBy = "profileImage")
    private Member member;

}
