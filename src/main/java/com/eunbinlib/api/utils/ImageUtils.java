package com.eunbinlib.api.utils;

import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImageUtils {

    public static BaseImageFile storeImage(String dirPath, MultipartFile image) {
        if (dirPath == null || image == null || image.isEmpty()) {
            return null;
        }

        try {
            String contentType = image.getContentType();

            if (contentType == null || isNotImage(contentType) || isNotVideo(contentType)) {
                // NOTE: In this app, video is same to an image.
                // NOTE: Because, this app will also show videos to the users of the app, like image.
                return null;
            }

            String originalFilename = image.getOriginalFilename();
            String storeFilename = createStoreFilename(originalFilename);

            image.transferTo(new File(getFullPath(dirPath, storeFilename)));

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

    public static List<BaseImageFile> saveImages(String dirPath, List<MultipartFile> images) {
        if (dirPath == null || images == null || images.isEmpty()) {
            return List.of();
        }

        List<BaseImageFile> result = new ArrayList<>();

        for (MultipartFile file : images) {
            BaseImageFile storedFile = storeImage(dirPath, file);

            if (storedFile != null) {
                result.add(storedFile);
            }
        }

        return result;
    }

    private static String createStoreFilename(String originalFilename) {
        String extension = extractExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return StringUtils.join(uuid, ".", extension);
    }

    private static String extractExtension(String originalFilename) {
        int index = originalFilename.lastIndexOf(".");
        return originalFilename.substring(index + 1);
    }

    private static String getFullPath(String dirPath, String filename) {
        return StringUtils.join(dirPath, filename);
    }

    private static boolean isNotImage(String contentType) {
        return !contentType.contains("image");
    }

    private static boolean isNotVideo(String contentType) {
        return !contentType.contains("video");
    }

}
