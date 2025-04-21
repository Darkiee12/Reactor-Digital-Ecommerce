package com.ecommerce.nashtech.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.nashtech.dto.CreateUserDto;
import com.ecommerce.nashtech.dto.UserDto;
import com.ecommerce.nashtech.errors.AccountError;
import com.ecommerce.nashtech.errors.UserError;
import com.ecommerce.nashtech.models.User;
import com.ecommerce.nashtech.services.UserService;
import com.ecommerce.nashtech.utils.JSON;
import com.ecommerce.nashtech.utils.rust.Option;
import com.ecommerce.nashtech.utils.rust.Result;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {
    private final UserService userService;

    @GetMapping("api/v1/user/uuid/{uuid}")
    public ResponseEntity<String> getUserByUuid(
            @Parameter(description = "UUID of the account to be retrieved", required = true, schema = @Schema(type = "string", format = "uuid", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")) @PathVariable UUID uuid) {
        var instance = "/api/v1/user/uuid/" + uuid;
        var user = userService.getUserByUuid(uuid);
        return switch (user) {
            case Option.Some<UserDto> u -> ResponseEntity.ok(u.get().toString());

            case Option.None<UserDto> err -> ResponseEntity.status(404)
                    .body(AccountError.AccountNotFoundError.build(Option.some(uuid.toString())).toErrResponse(instance));

        };
    }

    @PostMapping("api/v1/user")
    public ResponseEntity<String> createUser(@RequestBody CreateUserDto dto, BindingResult bindingResult){
        var instance = "/api/v1/user";
        var _resp = Result.fromBindingResult(bindingResult);
        return switch(_resp){
            case Result.Ok<Void, FieldError> ok -> {
                var user = userService.create(dto);
                yield switch(user){
                    case Result.Ok<User, ? extends AccountError> u -> {
                        var body = JSON.fromArgs("message", "User created successfully");
                        yield ResponseEntity.ok(body);
                    }
                    case Result.Err<User, ? extends AccountError> err -> {
                        var body = err.get();
                        yield switch(body){
                            case AccountError.DuplicateUsernameError e -> ResponseEntity.badRequest().body(AccountError.DuplicateUsernameError.build().toErrResponse(instance));
                            default -> ResponseEntity.badRequest().body(body.toErrResponse(instance));
                        };
                    }
                };
            }
            case Result.Err<Void, FieldError> err -> {
                var body = switch(err.get().getField()){
                    case "phoneNumber" -> UserError.PhoneNumberValidationError.build(Option.none()).toErrResponse(instance);
                    default -> JSON.fromArgs("message", "Invalid input data");
                };
                
                yield ResponseEntity.badRequest()
                        .body(body);
            }
        };
    }
}
