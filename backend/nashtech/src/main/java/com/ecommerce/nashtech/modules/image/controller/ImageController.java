package com.ecommerce.nashtech.modules.image.controller;

import java.util.UUID;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.nashtech.modules.image.model.Image;
import com.ecommerce.nashtech.modules.image.service.ImageService;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;
import com.ecommerce.nashtech.shared.util.Router;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ImageController implements IImageController {
    ImageService imageService;
    Router router = new Router("/api/v1/images");

    @GetMapping(value = "/{uuid}", produces = MediaType.MULTIPART_MIXED_VALUE)
    public Mono<ResponseEntity<MultiValueMap<String, HttpEntity<?>>>> getImage(@PathVariable("uuid") UUID uuid) {
        Flux<DataBuffer> image = imageService.getImage(uuid.toString());
        return imageService.getImageMetadata(uuid)
                .map(metadata -> {
                    var builder = new MultipartBodyBuilder();
                    builder.part("metadata", metadata, MediaType.APPLICATION_JSON);
                    builder.asyncPart("image", image, DataBuffer.class);
                    MultiValueMap<String, HttpEntity<?>> parts = builder.build();
                    return ResponseEntity.ok()
                            .contentType(MediaType.MULTIPART_MIXED)
                            .body(parts);
                });

    }

    private MediaType getMediaType(String mime) {
        switch (mime) {
            case "image/jpeg":
                return MediaType.IMAGE_JPEG;
            case "image/png":
                return MediaType.IMAGE_PNG;
            case "image/gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}
