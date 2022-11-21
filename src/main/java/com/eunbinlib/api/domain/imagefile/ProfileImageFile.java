package com.eunbinlib.api.domain.imagefile;

import com.eunbinlib.api.domain.common.BaseTimeEntity;
import com.eunbinlib.api.domain.user.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImageFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Embedded
    private BaseImageFile baseImageFile;

    @OneToOne(mappedBy = "profileImage")
    private Member member;
}
