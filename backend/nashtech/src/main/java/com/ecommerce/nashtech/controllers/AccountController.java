package com.ecommerce.nashtech.controllers;

import com.ecommerce.nashtech.dto.AccountDto;
import com.ecommerce.nashtech.dto.CreateAccountDTO;
import com.ecommerce.nashtech.dto.SignInDTO;
import com.ecommerce.nashtech.errors.AccountError;
import com.ecommerce.nashtech.models.Account;
import com.ecommerce.nashtech.security.jwt.JwtUtils;
import com.ecommerce.nashtech.services.AccountService;
import com.ecommerce.nashtech.utils.Settings;
import com.ecommerce.nashtech.utils.ApplicationResponse.ErrResponse;
import com.ecommerce.nashtech.utils.ApplicationResponse.LoginResponse;

import com.ecommerce.nashtech.utils.JSON;
import com.ecommerce.nashtech.utils.rust.Option;
import com.ecommerce.nashtech.utils.rust.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor()
@Tag(name = "Account Management", description = "Operations related to account management")
public class AccountController {

    private AccountService accountService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    @Operation(summary = "Get account by integer ID", description = "Retrieves an account using its integer ID. Returns the account details as a string if"
            + " found.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class), examples = @ExampleObject(value = "{\"id\":1, \"username\":\"john_doe\","
                    + " \"email\":\"john@example.com\"}"))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\":\"Account not found\",\"code\":\"404\"}"))) })
    @GetMapping("/api/v1/account/id/{id}")
    public ResponseEntity<String> getAccountById(
            @Parameter(description = "ID of the account to be retrieved", required = true, schema = @Schema(type = "integer", example = "1")) @PathVariable int id) {

        var instance = "/api/v1/account/id/" + id;
        var account = accountService.getById(id);
        switch (account) {
        case Option.Some<AccountDto> acc -> {
            return ResponseEntity.ok(acc.get().toString());
        }
        case Option.None<AccountDto> _err -> {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AccountError.AccountNotFoundError.build(Option.none()).toErrResponse(instance));
        }
        }
    }

    @Operation(summary = "Get account by UUID", description = "Retrieves an account using its UUID. Returns the account details as a string if found.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class), examples = @ExampleObject(value = "{\"uuid\":\"d290f1ee-6c54-4b01-90e6-d701748f0851\","
                    + " \"username\":\"jane_doe\"," + " \"email\":\"jane@example.com\"}"))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\":\"Account not found\",\"code\":\"404\"}"))) })
    @GetMapping("/api/v1/account/uuid/{uuid}")
    public ResponseEntity<String> getAccountByUUID(
            @Parameter(description = "UUID of the account to be retrieved", required = true, schema = @Schema(type = "string", format = "uuid", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")) @PathVariable UUID uuid) {

        var instance = "/api/v1/account/uuid/" + uuid;
        var account = accountService.getByUuid(uuid);
        return switch (account) {
        case Option.Some<AccountDto> acc -> ResponseEntity.ok(acc.get().toString());
        case Option.None<AccountDto> err -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(AccountError.AccountNotFoundError.build(Option.some(uuid.toString())).toErrResponse(instance));
        };
    }

    @Operation(summary = "Get account by username", description = "Retrieves an account using its username. Returns the account details as a string if"
            + " found.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class), examples = @ExampleObject(value = "{\"uuid\":\"d290f1ee-6c54-4b01-90e6-d701748f0851\","
                    + " \"username\":\"jane_doe\"," + " \"email\":\"jane@example.com\"}"))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\":\"Account not found\",\"code\":\"404\"}"))) })
    @GetMapping("/api/v1/account/username/{username}")
    public ResponseEntity<String> getAccountByUsername(
            @Parameter(description = "Username of the account to be retrieved", required = true, schema = @Schema(type = "string", example = "john_doe")) @PathVariable String username) {
        var instance = "/api/v1/account/username/" + username;
        var account = accountService.getByUsername(username);
        return switch (account) {
        case Option.Some<AccountDto> acc -> ResponseEntity.ok(acc.get().toString());
        case Option.None<AccountDto> err -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(AccountError.AccountNotFoundError.build(Option.none()).toErrResponse(instance));
        };
    }

    @Operation(summary = "Get all accounts", description = "Retrieve paginated accounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class), examples = @ExampleObject(value = "{\"content\": [{\"uuid\":1, \"username\":\"john_doe\","
                    + " \"email\":\"john@example.com\",\"createdAt\":\"1728829\"}],"
                    + " \"pageable\": {\"pageNumber\":0, \"pageSize\":10}," + " \"totalElements\":1}"))),
            @ApiResponse(responseCode = "404", description = "Accounts not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"No accounts found\", \"code\": \"404\"}"))) })
    @GetMapping("/api/v1/account")
    public ResponseEntity<Page<AccountDto>> getAllAccounts(
            @Parameter(description = "Page number of the paginated accounts list", schema = @Schema(type = "integer", defaultValue = Settings.Pagination.DEFAULT_PAGE, example = "0")) @RequestParam(defaultValue = Settings.Pagination.DEFAULT_PAGE) int page,
            @Parameter(description = "Page size of the paginated accounts list", schema = @Schema(type = "integer", defaultValue = Settings.Pagination.DEFAULT_SIZE, example = "10")) @RequestParam(defaultValue = Settings.Pagination.DEFAULT_SIZE) int size) {

        Pageable pageable = PageRequest.of(page, size);
        var accounts = accountService.getAll(pageable);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Create a new account", description = "Create a new account")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = CreateAccountDTO.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = "{\"message\":\"Account created successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = "{\"message\": \"Invalid input data\", \"code\": \"400\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized access\"}"))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = "{\"message\": \"Unprocessable entity\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = "{\"message\": \"Internal server error\"}"))) })
    @PostMapping("/api/v1/account/register")
    public ResponseEntity<String> createAccount(@Valid @RequestBody CreateAccountDTO accountDTO,
            BindingResult response) {

        final var instance = "/api/v1/account/register";
        var _response = Result.fromBindingResult(response);
        switch (_response) {
        case Result.Ok<Void, FieldError> ok -> {
            var result = accountService.create(accountDTO);
            switch (result) {
            case Result.Ok<Account, AccountError> acc -> {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(JSON.fromArgs("message", "Account created successfully"));
            }
            case Result.Err<Account, AccountError> err -> {
                return ResponseEntity.badRequest().body(err.get().toErrResponse(instance));
            }
            }
        }
        case Result.Err<Void, FieldError> err -> {
            var body = switch (err.get().getField()) {
            case "email" -> AccountError.NotValidEmail.build().toErrResponse(instance);
            case "username" -> AccountError.DuplicateUsernameError.build().toErrResponse(instance);
            case "password" -> AccountError.NotValidPassword.build().toErrResponse(instance);
            default -> JSON.fromArgs("message", "Invalid input data");
            };
            return ResponseEntity.badRequest().body(body);
        }
        }
    }

    @Operation(summary = "Login to account", description = "Login to account")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = SignInDTO.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class), examples = @ExampleObject(value = LoginResponse.ExampleObject))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrResponse.class), examples = @ExampleObject(value = ErrResponse.ExampleObject))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrResponse.class), examples = @ExampleObject(value = ErrResponse.ExampleObject))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrResponse.class), examples = @ExampleObject(value = ErrResponse.ExampleObject))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrResponse.class), examples = @ExampleObject(value = ErrResponse.ExampleObject))) })
    @PostMapping("/api/v1/account/login")
    public ResponseEntity<String> login(
        @Valid @RequestBody SignInDTO dto, 
        BindingResult response
        // ServerWebExchange exchange
    ) {
        final var instance = "/api/v1/account/login";
        var _response = Result.fromBindingResult(response);
        return switch (_response) {
        case Result.Ok<Void, FieldError> ok -> {
            var res = Result
                    .wrap(() -> authenticationManager
                            .authenticate(new UsernamePasswordAuthenticationToken(dto.username(), dto.password())))
                    .map(auth -> {
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        return auth;
                    });

            yield switch (res) {
                case Result.Ok<Authentication, ? extends Exception> okAuth -> {
                    Authentication auth = okAuth.get();
                    String jwt = jwtUtils.generateTokenForUser(auth);
                    var output = LoginResponse.build(jwt);
                //     ResponseCookie cookie = ResponseCookie.from("token", jwt)
                //         .httpOnly(true)
                //         .secure(Settings.Environment.isProduction()) // Make sure it's only sent over HTTPS
                //         .path("/")
                //         .sameSite("None") // Required for cross-origin
                //         .maxAge(Duration.ofMillis(jwtUtils.getExpirationMs()))
                //         .build();
                // exchange.getResponse().addCookie(cookie);
                        
                    yield ResponseEntity.ok(output);
                }
                case Result.Err<Authentication, ? extends Exception> err -> {
                    yield ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(AccountError.WrongCredentialsError.build().toErrResponse(instance));
                }
            };

        }
        case Result.Err<Void, FieldError> err -> {
            yield ResponseEntity.badRequest().body(ErrResponse.customError(instance, "message", "Invalid input data"));
        }
        };
    }
}
