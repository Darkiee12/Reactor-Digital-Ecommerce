package com.ecommerce.nashtech.services;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.nashtech.dto.CreateUserDto;
import com.ecommerce.nashtech.dto.UserDto;
import com.ecommerce.nashtech.errors.AccountError;
import com.ecommerce.nashtech.errors.UserError;
import com.ecommerce.nashtech.mappers.GetUserDtoMapper;
import com.ecommerce.nashtech.models.Account;
import com.ecommerce.nashtech.models.User;
import com.ecommerce.nashtech.repositories.UserRepository;
import com.ecommerce.nashtech.utils.rust.Option;
import com.ecommerce.nashtech.utils.rust.Result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import com.google.i18n.phonenumbers.NumberParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final GetUserDtoMapper getUserDtoMapper;

    public Option<UserDto> getUserByUuid(UUID uuid) {
        return Option.fromOptional(userRepository.findByAccountUuid(uuid).map(getUserDtoMapper));
    }

    @Transactional
    public Result<User, ? extends AccountError> create(CreateUserDto dto) {
        var accountDto = dto.toCreateAccountDTO();
        var validatedUser = UserValidation.validate(dto);
        switch (validatedUser) {
        case Result.Ok<User, UserError> userOk -> {
            var account = accountService.create(accountDto);
            switch (account) {
            case Result.Ok<Account, AccountError> accountOk -> {
                var user = userOk.get();
                user.setAccount(accountOk.get());
                userRepository.save(user);
                return Result.ok(user);
            }
            case Result.Err<Account, AccountError> err -> {
                return Result.err(err.get());
            }
            }
        }
        case Result.Err<User, UserError> err -> {
            log.error("User validation failed: {}", err.get().getMessage());
            return Result.err(err.get());
        }
        }
    }

    public Result<User, UserError> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return Option.fromOptional(userRepository.findByAccountUsername(username))
                .okOr(UserError.UserNotFoundError.build(username));
    }
}

record Pair<T, U>(T first, U second) {
}

class UserValidation {

    private static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public static Result<String, UserError> validatePhoneNumber(String regionCode, String phoneNumber) {
        var fullNumber = regionCode + phoneNumber;
        Result<PhoneNumber, NumberParseException> proto = Result.wrap(() -> phoneUtil.parse(fullNumber, null));
        switch (proto) {
        case Result.Ok<PhoneNumber, NumberParseException> ok -> {
            if (phoneUtil.isValidNumber(ok.get())) {
                String formatted = phoneUtil.format(ok.get(), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                return Result.ok(formatted);
            } else {
                return Result.err(UserError.PhoneNumberValidationError.build(Option.some(regionCode + phoneNumber)));
            }
        }
        case Result.Err<PhoneNumber, NumberParseException> err -> {
            return Result.err(UserError.PhoneNumberValidationError.build(Option.some(regionCode + phoneNumber)));
        }
        }
    }

    private static String queryBuilder(String city, String state, String country) {
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

    public static Result<String, UserError> validateAddress(String address, String city, String state, String country) {
        String query = queryBuilder(city, state, country);
        URI uri = UriComponentsBuilder.newInstance().scheme("https").host("nominatim.openstreetmap.org")
                .path("/search.php").queryParam("q", URLEncoder.encode(query, StandardCharsets.UTF_8))
                .queryParam("format", "json").build(true).toUri();

        HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();

        var response = client.get().uri(uri).retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        ClientResponse::createException)
                .bodyToMono(JsonNode[].class);
        return switch (Result.wrap(() -> response.block())) {
        case Result.Ok<JsonNode[], RuntimeException> ok -> Option.fromOptional(Arrays.stream(ok.get()).filter(location -> {
            var addressType = location.get("addresstype").asText();
            return addressType.equals("city") || addressType.equals("state") || addressType.equals("country");
        }).map(location -> String.join(",", address, location.get("display_name").asText())).findFirst())
                .okOr(UserError.AddressValidationError.build(query));
        case Result.Err<JsonNode[], RuntimeException> err -> Result.err(UserError.AddressValidationError.build(query));
        };
    }

    public static Result<User, UserError> validate(CreateUserDto dto) {
        var phoneNumber = validatePhoneNumber(dto.phoneNumberRegionCode(), dto.phoneNumber());
        var address = validateAddress(dto.address(), dto.city(), dto.state(), dto.country());
        Pair<Result<String, UserError>, Result<String, UserError>> pair = new Pair<>(phoneNumber, address);
        return switch (pair) {
        case Pair(Result.Ok<String, UserError> phoneNum, Result.Ok<String, UserError> addr) -> {
            var partialUser = dto.toPartialUser();
            partialUser.setPhoneNumber(phoneNum.get());
            partialUser.setAddress(addr.get());
            yield Result.ok(partialUser);
        }
        case Pair(Result.Err<String, UserError> phoneNum, Result.Ok<String, UserError> addr) -> {
            yield Result.err(phoneNum.get());
        }
        case Pair(Result.Ok<String, UserError> phoneNum, Result.Err<String, UserError> addr) -> {
            yield Result.err(addr.get());
        }
        case Pair(Result.Err<String, UserError> phoneNum, Result.Err<String, UserError> addr) -> {
            yield Result.err(phoneNum.get());
        }
        default -> throw new IllegalStateException("Unreachable statement: " + pair);
        };

    }

}
