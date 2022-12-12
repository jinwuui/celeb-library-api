package com.eunbinlib.api.application.controller;

import com.eunbinlib.api.application.dto.request.ImageReadRequest;
import com.eunbinlib.api.application.dto.response.ImageResponse;
import com.eunbinlib.api.application.dto.response.PaginationResponse;
import com.eunbinlib.api.application.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 앱의 홈 화면에서 사진만 볼 수 있도록 페이지네이션을 제공해주는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageFileController {

    private final ImageFileService imageFileService;

    @GetMapping()
    public PaginationResponse<ImageResponse> readMany(@ModelAttribute ImageReadRequest imageReadRequest) {
        return imageFileService.readMany(imageReadRequest);
    }
}
