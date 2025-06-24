package com.example.claude_backend.infrastructure.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * AWS S3 서비스 클래스
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Service
public class S3Service {

    @Value("${aws.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    /**
     * S3 객체의 URL을 생성합니다.
     *
     * @param key 객체 키
     * @return S3 URL
     */
    public String getObjectUrl(String key) {
        log.debug("S3 객체 URL 생성. bucket: {}, key: {}", bucket, key);
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }

    /**
     * 배경 파일의 URL을 생성합니다.
     *
     * @param backgroundCode 배경 코드 (예: "01")
     * @return 배경 파일 URL
     */
    public String getBackgroundUrl(String backgroundCode) {
        log.debug("배경 파일 URL 생성. backgroundCode: {}", backgroundCode);
        return getObjectUrl("map/" + backgroundCode + ".png");
    }

    /**
     * 캐릭터 파일의 URL을 생성합니다.
     *
     * @param characterCode 캐릭터 코드 (예: "001")
     * @return 캐릭터 파일 URL
     */
    public String getCharacterUrl(String characterCode) {
        log.debug("캐릭터 파일 URL 생성. characterCode: {}", characterCode);
        return getObjectUrl("char/" + characterCode + ".gif");
    }
}