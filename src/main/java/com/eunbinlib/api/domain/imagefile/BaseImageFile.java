package com.eunbinlib.api.domain.imagefile;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class BaseImageFile {

    @NotNull
    private String storedFilename;

    @NotNull
    private String originalFilename;

    @NotNull
    private String contentType;

    @NotNull
    private Long byteSize;

    @Builder
    public BaseImageFile(String storedFilename, String originalFilename, String contentType, Long byteSize) {
        this.storedFilename = storedFilename;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.byteSize = byteSize;
    }
}
