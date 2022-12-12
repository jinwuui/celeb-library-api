package com.eunbinlib.api.application.dto.response;

import com.eunbinlib.api.application.domain.imagefile.PostImageFile;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ImageResponse {

    private final Long id;

    private final String imageUrl;

    @Builder
    public ImageResponse(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public ImageResponse(PostImageFile postImageFile) {
        this(postImageFile.getId(), postImageFile.getBaseImageFile().getStoredFilename());
    }
}
