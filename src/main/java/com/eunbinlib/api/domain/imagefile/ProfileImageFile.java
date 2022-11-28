package com.eunbinlib.api.domain.imagefile;

import com.eunbinlib.api.domain.BaseTimeEntity;
import com.eunbinlib.api.domain.user.Member;
import lombok.AccessLevel;
import lombok.Builder;
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

    @OneToOne(mappedBy = "profileImageFile")
    private Member member;

    @Builder
    public ProfileImageFile(final BaseImageFile baseImageFile, final Member member) {
        this.baseImageFile = baseImageFile;
        this.member = member;
    }
}
