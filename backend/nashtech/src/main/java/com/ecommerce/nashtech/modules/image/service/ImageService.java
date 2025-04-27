package com.ecommerce.nashtech.modules.image.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.modules.image.internal.repository.ImageRepository;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Service
public class ImageService {
    ImageRepository imageRepo;
    MinioClient minioClient;
    @Value("${minio.bucket.name}")
    @NonFinal
    String bucketName;
}
