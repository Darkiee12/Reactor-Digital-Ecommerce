package com.ecommerce.nashtech.modules.image.controller;

import java.util.UUID;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.ecommerce.nashtech.modules.image.error.ImageError;
import com.ecommerce.nashtech.modules.image.service.ImageService;
import com.ecommerce.nashtech.shared.response.ErrorResponse;
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

    @Override
    @GetMapping(value = "/uuid/{uuid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Flux<DataBuffer> getImage(@PathVariable("uuid") UUID uuid, ServerWebExchange exchange) {
        return imageService.getImage(uuid.toString());
    }

    @Override
    @GetMapping(value = "/key/{objectKey}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Flux<DataBuffer> getImage(@PathVariable("objectKey") String objectKey, ServerWebExchange exchange) {
        return imageService.getImage(objectKey);
    }

    @Override
    @GetMapping(value = "/uuid/{uuid}/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> getMetadata(@PathVariable("uuid") UUID uuid) {
        var instance = router.getURI(uuid, "metadata");
        return imageService.getImageMetadata(uuid)
                .map(metatadata -> SuccessfulResponse.WithData.builder().item(metatadata).instance(instance).build()
                        .asResponse())
                .onErrorResume(ImageError.class, e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

}
