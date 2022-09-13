package com.eunbinlib.api.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Slf4j
public class MultiValueMapper {

    public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object object) {
        try {
            MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
            Map<String, String> fieldMap = objectMapper.convertValue(object, new TypeReference<>() {
            });
            valueMap.setAll(fieldMap);

            return valueMap;
        } catch (Exception e) {
            log.error("error MultiValueMapper", e);
            throw new IllegalArgumentException("MultiValueMapper 오류 발생");
        }
    }

}
