package com.eunbinlib.api.application.utils;

import com.eunbinlib.api.application.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.application.exception.type.EunbinlibIllegalArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImageUtils {

    // TODO: change ImageUtils to ImageService/ImageRepository
    private static final String TMP_DIR_PATH = "/Users/jinwoo/coding/spring/eunbinlib/src/main/resources/static/images/tmp/";

    public static BaseImageFile storeImage(final MultipartFile image) {
        validateDirPath(TMP_DIR_PATH);
        validateImage(image);
        validateContentType(image.getContentType());

        try {
            String originalFilename = image.getOriginalFilename();
            String storeFilename = createStoreFilename(originalFilename);

            image.transferTo(new File(getFullPath(TMP_DIR_PATH, storeFilename)));

            return BaseImageFile.builder()
                    .storedFilename(storeFilename)
                    .originalFilename(originalFilename)
                    .contentType(image.getContentType())
                    .byteSize(image.getSize())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<BaseImageFile> storeImages(final List<MultipartFile> images) {
        if (CollectionUtils.isEmpty(images)) {
            return List.of();
        }

        List<BaseImageFile> result = new ArrayList<>();

        for (MultipartFile file : images) {
            BaseImageFile storedFile = storeImage(file);

            if (storedFile != null) {
                result.add(storedFile);
            }
        }

        return result;
    }

    private static String createStoreFilename(final String originalFilename) {
        String extension = extractExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return StringUtils.join(uuid, ".", extension);
    }

    private static String extractExtension(final String originalFilename) {
        int index = originalFilename.lastIndexOf(".");
        return originalFilename.substring(index + 1);
    }

    private static String getFullPath(final String dirPath, final String filename) {
        return StringUtils.join(dirPath, filename);
    }

    private static boolean isNotImage(final String contentType) {
        return !contentType.contains("image");
    }

    private static boolean isNotVideo(final String contentType) {
        return !contentType.contains("video");
    }

    private static void validateDirPath(String dirPath) {
        if (dirPath == null) {
            throw new EunbinlibIllegalArgumentException("잘못된 저장 위치가 입력되었습니다.");
        }
    }

    private static void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new EunbinlibIllegalArgumentException("image", "사진이 존재하지 않습니다.");
        }
    }

    private static void validateContentType(String contentType) {
        if (contentType == null || (isNotImage(contentType) && isNotVideo(contentType))) {
            // NOTE: In this app, video is same to an image.
            // NOTE: Because, this app will also show videos to the users of the app, like image.
            throw new EunbinlibIllegalArgumentException("contentType", "이미지/비디오 형식의 파일을 넣어주세요.");
        }
    }
}
