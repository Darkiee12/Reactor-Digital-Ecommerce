package com.ecommerce.nashtech.modules.image.service;

import com.ecommerce.nashtech.modules.image.error.ImageError;
import com.ecommerce.nashtech.modules.image.internal.repository.ImageRepository;
import com.ecommerce.nashtech.modules.image.model.Image;
import com.ecommerce.nashtech.shared.types.Option;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class ImageService {
    ImageRepository imageRepo;
    MinioClient minioClient;
    @Value("${minio.bucket.name}")
    @NonFinal
    String bucketName;
    DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
    int BUFFER_SIZE = 8192;

    public Mono<Image> uploadImage(FilePart filePart, String altText) {
        return processAndSave(filePart, altText);
    }

    public Mono<Image> uploadImage(FilePart logo, String altText, String objectKey) {
        return processAndSave(logo, altText, objectKey);
    }

    public Flux<Image> uploadImages(Flux<FilePart> fileParts, String altText) {
        return fileParts.flatMap(part -> processAndSave(part, altText));
    }

    public Mono<Flux<DataBuffer>> getImageByUuid(UUID uuid) {
        return imageRepo.findByUuid(uuid)
                .switchIfEmpty(Mono.error(ImageError.ImageNotFoundError.build(Option.some(uuid.toString()))))
                .flatMap(image -> {
                    GetObjectArgs args = GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(image.getObjectKey())
                            .build();
                    return Mono.fromCallable(() -> minioClient.getObject(args))
                            .flatMap(inputStream -> {
                                return Mono.just(DataBufferUtils.read((Resource) inputStream,
                                        new DefaultDataBufferFactory(), 4096));
                            })
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

    public Flux<DataBuffer> getImage(String objectKey) {
        return Mono.fromCallable(() -> minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectKey)
                        .build()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(inputStream -> DataBufferUtils.readInputStream(() -> inputStream, dataBufferFactory,
                        BUFFER_SIZE));
    }

    public Mono<Image> getImageMetadata(UUID uuid) {
        return imageRepo.findByUuid(uuid)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Image metadata not found for UUID: " + uuid)));
    }

    public Flux<Image> getImageMetadata(Flux<UUID> uuids) {
        return uuids.flatMap(this::getImageMetadata);
    }

    /**
     * Core process: read bytes, upload to MinIO, extract dimensions, save metadata.
     */
    private Mono<Image> processAndSave(FilePart filePart, String altText) {

        String mimeType = filePart.headers().getContentType() != null
                ? filePart.headers().getContentType().toString()
                : "application/octet-stream";

        UUID uuid = UUID.randomUUID();
        String originalFilename = uuid.toString();
        String objectKey = uuid.toString();
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return Mono.fromCallable(
                            () -> handleUploadAndMetadata(bytes, originalFilename, mimeType, altText, uuid, objectKey))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .flatMap(imageRepo::save)
                .doOnSuccess(saved -> log.info("Uploaded image {} with key {}", saved.getId(), saved.getObjectKey()));
    }

    private Mono<Image> processAndSave(FilePart filePart, String altText, String objectKey) {

        String mimeType = filePart.headers().getContentType() != null
                ? filePart.headers().getContentType().toString()
                : "application/octet-stream";

        UUID uuid = UUID.randomUUID();
        String originalFilename = uuid.toString();
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return Mono.fromCallable(
                            () -> handleUploadAndMetadata(bytes, originalFilename, mimeType, altText, uuid, objectKey))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .flatMap(imageRepo::save)
                .doOnSuccess(saved -> log.info("Uploaded image {} with key {}", saved.getId(), saved.getObjectKey()));
    }

    private Image handleUploadAndMetadata(
            byte[] bytes,
            String originalFilename,
            String mimeType,
            String altText,
            UUID uuid,
            String objectKey) throws Exception {

        System.out.println("Uploading image to MinIO: " + objectKey);
        try (InputStream uploadStream = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(uploadStream, bytes.length, -1)
                    .contentType(mimeType)
                    .build());
        }
        System.out.println("Image uploaded to MinIO: " + objectKey);

        int width = 0, height = 0;
        try (InputStream imageStream = new ByteArrayInputStream(bytes)) {
            BufferedImage bufferedImage = ImageIO.read(imageStream);
            if (bufferedImage != null) {
                width = bufferedImage.getWidth();
                height = bufferedImage.getHeight();
            }
        }

        return Image.builder()
                .uuid(uuid)
                .name(originalFilename)
                .alt(altText)
                .objectKey(objectKey)
                .mimeType(mimeType)
                .size((long) bytes.length)
                .width(width)
                .height(height)
                .uploadedAt(System.currentTimeMillis())
                .build();
    }
}
