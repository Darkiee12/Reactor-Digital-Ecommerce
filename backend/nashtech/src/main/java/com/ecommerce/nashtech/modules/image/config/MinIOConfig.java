package com.ecommerce.nashtech.modules.image.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Configuration
public class MinIOConfig {

    @Value("${minio.url}")
    String minioUrl;

    @Value("${minio.access.name}")
    String accessKey;

    @Value("${minio.access.secret}")
    String secretKey;

    @Value("${minio.bucket.name}")
    String bucketName;

    @Bean
    public MinioClient minioClient() throws MinioException {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}
