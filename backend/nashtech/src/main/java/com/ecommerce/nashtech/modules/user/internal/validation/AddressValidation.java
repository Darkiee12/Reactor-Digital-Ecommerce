package com.ecommerce.nashtech.modules.user.internal.validation;

import java.time.Duration;
import java.util.function.Function;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import com.ecommerce.nashtech.modules.user.error.UserError;
import com.ecommerce.nashtech.modules.user.internal.types.RawAddress;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
public class AddressValidation implements Function<RawAddress, Mono<String>> {

    private final WebClient webClient;

    public AddressValidation() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5000))
                        .addHandlerLast(new WriteTimeoutHandler(5000)));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "EcommerceValidationService/1.0")
                .build();
    }

    @Override
    public Mono<String> apply(RawAddress addr) {
        String city = addr.city();
        String state = addr.state();
        String country = addr.country();
        String address = addr.address();

        return webClient.get()
                .uri((UriBuilder uriBuilder) -> {
                    UriBuilder b = uriBuilder.path("/search");
                    if (city != null && !city.isBlank()) {
                        b = b.queryParam("city", city);
                    }
                    if (state != null && !state.isBlank()) {
                        b = b.queryParam("state", state);
                    }
                    if (country != null && !country.isBlank()) {
                        b = b.queryParam("country", country);
                    }
                    return b.queryParam("format", "json").build();
                })
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        ClientResponse::createException)
                .bodyToMono(JsonNode[].class)
                .flatMap(jsonNodes -> Mono.justOrEmpty(findValidLocation(jsonNodes, address))
                        .switchIfEmpty(Mono.error(
                                UserError.AddressValidationError.build(
                                        "Invalid address: city=" + city + ", state=" + state + ", country="
                                                + country))))
                .onErrorResume(e -> Mono.error(
                        UserError.AddressValidationError.build(
                                "Error during address validation: city=" + city + ", state=" + state + ", country="
                                        + country + " (" + e.getMessage() + ")")));
    }

    private String findValidLocation(JsonNode[] jsonNodes, String address) {
        if (jsonNodes == null) {
            return null;
        }
        for (JsonNode location : jsonNodes) {
            JsonNode displayNameNode = location.get("display_name");
            if (displayNameNode != null && displayNameNode.isTextual()) {
                return String.join(", ", address, displayNameNode.asText());
            }
        }
        return null;
    }
}
