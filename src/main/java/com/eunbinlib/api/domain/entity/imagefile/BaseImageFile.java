package com.eunbinlib.api.domain.entity.imagefile;

import com.eunbinlib.api.domain.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseImageFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String savedFileName;

    @NotNull
    private String originalFileName;

    @NotNull
    private String extension;

    @NotNull
    private Integer widthPixel;

    @NotNull
    private Integer heightPixel;

    @NotNull
    private Long byteSize;

}
