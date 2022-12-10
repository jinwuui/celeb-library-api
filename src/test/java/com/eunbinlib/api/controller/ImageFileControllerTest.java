package com.eunbinlib.api.controller;

import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.imagefile.PostImageFile;
import com.eunbinlib.api.dto.request.ImageReadRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.eunbinlib.api.auth.data.AuthProperties.AUTHORIZATION_HEADER;
import static com.eunbinlib.api.auth.data.AuthProperties.TOKEN_PREFIX;
import static com.eunbinlib.api.testutils.MultiValueMapper.convert;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ImageFileControllerTest extends ControllerTest {

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
    @DisplayName("이미지 조회")
    class Read {

        @Test
        @DisplayName("이미지 여러개 조회")
        void readMany() throws Exception {
            // given
            loginMember();

            ImageReadRequest request = new ImageReadRequest(null, 5);

            MultiValueMap<String, String> params = convert(objectMapper, request);

            // expected
            mockMvc.perform(get("/api/images")
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..['data'].length()", is(5)))
                    .andExpect(jsonPath("$..['meta'].size").value(5))
                    .andExpect(jsonPath("$..['meta'].hasMore").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("이미지 여러개 조회 - size가 10000")
        void readManyEdgeCase() throws Exception {

            // given
            loginMember();

            ImageReadRequest request = new ImageReadRequest(null, 10000);

            MultiValueMap<String, String> params = convert(objectMapper, request);

            // expected
            mockMvc.perform(get("/api/images")
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..['data'].length()", is(postImageFiles.size())))
                    .andExpect(jsonPath("$..['meta'].size").value(postImageFiles.size()))
                    .andExpect(jsonPath("$..['meta'].hasMore").value(false))
                    .andDo(print());
        }


        @Test
        @DisplayName("이미지 여러개 조회 - after가 15, size가 10")
        void readManyAfterAndSize() throws Exception {
            // given
            loginMember();
            List<PostImageFile> all = postImageFileRepository.findAll();
            PostImageFile latestImageFile = all.get(all.size() - 1);

            ImageReadRequest request = new ImageReadRequest(latestImageFile.getId() - 10L, 10);

            MultiValueMap<String, String> params = convert(objectMapper, request);

            // expected
            mockMvc.perform(get("/api/images")
                            .params(params)
                            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + memberAccessToken)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..['data'].length()", is(request.getSize())))
                    .andExpect(jsonPath("$..['meta'].size").value(request.getSize()))
                    .andExpect(jsonPath("$..['meta'].hasMore").value(true))
                    .andDo(print());
        }
    }
}
