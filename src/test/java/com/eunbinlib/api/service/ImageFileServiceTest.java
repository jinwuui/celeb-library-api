package com.eunbinlib.api.service;

import com.eunbinlib.api.application.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.application.domain.imagefile.PostImageFile;
import com.eunbinlib.api.application.dto.request.ImageReadRequest;
import com.eunbinlib.api.application.dto.response.ImageResponse;
import com.eunbinlib.api.application.dto.response.PaginationMeta;
import com.eunbinlib.api.application.dto.response.PaginationResponse;
import com.eunbinlib.api.application.service.ImageFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageFileServiceTest extends ServiceTest {

    @Autowired
    ImageFileService imageFileService;

    List<PostImageFile> postImageFiles;

    @BeforeEach
    void setting() {
        postImageFileRepository.deleteAll();

        postImageFiles = IntStream.range(0, 30)
                .mapToObj(i -> PostImageFile.builder()
                        .baseImageFile(
                                BaseImageFile.builder()
                                        .originalFilename(i + "originalfilename.jpg")
                                        .storedFilename(i + "storedfilename.jpg")
                                        .build())
                        .build())
                .collect(Collectors.toList());

        postImageFileRepository.saveAll(postImageFiles);
    }

    @Nested
    @DisplayName("이미지 조회 테스트")
    class Read {

        @Test
        @DisplayName("이미지 페이지네이션 조회 - after null이면 처음부터 조회")
        void readMany() {
            // given
            ImageReadRequest request = new ImageReadRequest(null, 5);

            // when
            PaginationResponse<ImageResponse> result = imageFileService.readMany(request);

            PaginationMeta meta = result.getMeta();
            List<ImageResponse> data = result.getData();

            // then
            assertThat(meta.getSize()).isEqualTo(5);
            assertThat(meta.getHasMore()).isEqualTo(true);
            assertThat(data.get(0)
                    .getImageUrl()
                    .contains(
                            postImageFiles.get(29)
                                    .getBaseImageFile()
                                    .getStoredFilename()
                    )
            )
                    .isTrue();
            assertThat(data.get(data.size() - 1)
                    .getImageUrl()
                    .contains(
                            postImageFiles.get(25)
                                    .getBaseImageFile()
                                    .getStoredFilename()
                    )
            )
                    .isTrue();
        }

        @Test
        @DisplayName("이미지 페이지네이션 조회 - 기본값으로 조회")
        void readManyDefaultValue() {
            // given
            ImageReadRequest request = new ImageReadRequest();

            // when
            PaginationResponse<ImageResponse> result = imageFileService.readMany(request);

            PaginationMeta meta = result.getMeta();
            List<ImageResponse> data = result.getData();

            // then
            assertThat(meta.getSize()).isEqualTo(20);
            assertThat(meta.getHasMore()).isEqualTo(true);
            assertThat(data.get(0)
                    .getImageUrl()
                    .contains(
                            postImageFiles.get(29)
                                    .getBaseImageFile()
                                    .getStoredFilename()
                    )
            )
                    .isTrue();
            assertThat(data.get(data.size() - 1)
                    .getImageUrl()
                    .contains(
                            postImageFiles.get(10)
                                    .getBaseImageFile()
                                    .getStoredFilename()
                    )
            )
                    .isTrue();
        }

        @Test
        @DisplayName("이미지 페이지네이션 조회 - after 이후부터 조회")
        void readManyAfter() {
            // given
            ImageReadRequest request = new ImageReadRequest(15L, 10);

            // when
            PaginationResponse<ImageResponse> result = imageFileService.readMany(request);

            PaginationMeta meta = result.getMeta();
            List<ImageResponse> data = result.getData();

            // then
            assertThat(meta.getSize()).isEqualTo(10);
            assertThat(meta.getHasMore()).isEqualTo(true);
            assertThat(data.get(0).getId()).isEqualTo(14L);
        }

        @Test
        @DisplayName("이미지 페이지네이션 조회 - 더 이상 조회 불가능 (남아있는 개수가 요청 개수보다 적을 때)")
        void readManyNoMore() {
            // given
            ImageReadRequest request = new ImageReadRequest(null, postImageFiles.size() + 10);

            // when
            PaginationResponse<ImageResponse> result = imageFileService.readMany(request);

            PaginationMeta meta = result.getMeta();

            // then
            assertThat(meta.getSize()).isEqualTo(postImageFiles.size());
            assertThat(meta.getHasMore()).isEqualTo(false);
        }

        @Test
        @DisplayName("이미지 페이지네이션 조회 - 더 이상 조회 불가능 (남아있는 개수 == 요청 개수)")
        void readManyNoMoreEdgeCase() {

            // given
            ImageReadRequest request = new ImageReadRequest(null, postImageFiles.size());

            // when
            PaginationResponse<ImageResponse> result = imageFileService.readMany(request);

            PaginationMeta meta = result.getMeta();

            // then
            assertThat(meta.getSize()).isEqualTo(30);
            assertThat(meta.getHasMore()).isEqualTo(false);
        }
    }
}
