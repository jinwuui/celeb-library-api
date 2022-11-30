package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.repository.postimagefile.PostImageFileRepository;
import com.eunbinlib.api.dto.request.ImageReadRequest;
import com.eunbinlib.api.dto.response.ImageResponse;
import com.eunbinlib.api.dto.response.PaginationMeta;
import com.eunbinlib.api.dto.response.PaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageFileService {

    private final PostImageFileRepository postImageFileRepository;

    public PaginationResponse<ImageResponse> readMany(ImageReadRequest imageReadRequest) {
        List<ImageResponse> data = postImageFileRepository.getList(
                        imageReadRequest.getLimit(),
                        imageReadRequest.getAfter()
                )
                .stream()
                .map(ImageResponse::new)
                .collect(Collectors.toList());

        PaginationMeta meta = PaginationMeta.builder()
                .size(data.size())
                .hasMore(isHasMore(data))
                .build();

        return PaginationResponse.<ImageResponse>builder()
                .meta(meta)
                .data(data)
                .build();
    }

    private boolean isHasMore(List<ImageResponse> data) {
        return !data.isEmpty() && postImageFileRepository.existsNext(data.get(data.size() - 1).getId());
    }
}
