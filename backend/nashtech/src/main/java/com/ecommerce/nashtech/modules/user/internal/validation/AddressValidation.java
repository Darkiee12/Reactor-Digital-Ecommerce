package com.ecommerce.nashtech.modules.user.internal.validation;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

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
    @Override
    public Mono<String> apply(RawAddress addr) {
        String city = addr.city();
        String state = addr.state();
        String country = addr.country();
        String address = addr.address();
        String query = queryBuilder(city, state, country);
        URI uri = UriComponentsBuilder.newInstance().scheme("https").host("nominatim.openstreetmap.org")
                .path("/search.php").queryParam("q", URLEncoder.encode(query, StandardCharsets.UTF_8))
                .queryParam("format", "json").build(true).toUri();

        HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000)).doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5000)).addHandlerLast(new WriteTimeoutHandler(5000)));

        WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(uri.toString()).build();

        return client.get().retrieve().onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                ClientResponse::createException).bodyToMono(JsonNode[].class).flatMap(jsonNodes -> {
                    return Mono.justOrEmpty(findValidLocation(jsonNodes, address)).switchIfEmpty(
                            Mono.error(UserError.AddressValidationError.build("Invalid address: " + query)));
                }).onErrorResume(e -> Mono
                        .error(UserError.AddressValidationError.build("Error during address validation: " + query)));
    }

    private String queryBuilder(String city, String state, String country) {
        List<String> parts = new ArrayList<>();
        if (city != null && !city.isBlank()) {
            parts.add(city);
        }
        if (state != null && !state.isBlank()) {
            parts.add(state);
        }
        parts.add(country);

        return String.join(", ", parts);
    }

    private String findValidLocation(JsonNode[] jsonNodes, String address) {
        for (JsonNode location : jsonNodes) {
            String addressType = location.get("addresstype").asText();
            if ("city".equals(addressType) || "state".equals(addressType) || "country".equals(addressType)) {
                return String.join(",", address, location.get("display_name").asText());
            }
        }
        return null;
    }
}
