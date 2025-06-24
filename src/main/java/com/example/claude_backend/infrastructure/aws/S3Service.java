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
     * 맵 파일의 URL을 생성합니다.
     *
     * @param mapName 맵 이름
     * @return 맵 파일 URL
     */
    public String getMapUrl(String mapName) {
        log.debug("맵 파일 URL 생성. mapName: {}", mapName);
        return getObjectUrl("maps/" + mapName + ".json");
    }

    /**
     * 캐릭터 파일의 URL을 생성합니다.
     *
     * @param characterName 캐릭터 이름
     * @return 캐릭터 파일 URL
     */
    public String getCharacterUrl(String characterName) {
        log.debug("캐릭터 파일 URL 생성. characterName: {}", characterName);
        return getObjectUrl("characters/" + characterName + ".png");
    }
}