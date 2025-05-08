package com.ecommerce.nashtech.modules.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.ecommerce.nashtech.modules.account.service.AccountService;
import com.ecommerce.nashtech.modules.account.service.RoleService;
import com.ecommerce.nashtech.modules.user.dto.*;
import com.ecommerce.nashtech.modules.user.error.UserError;
import com.ecommerce.nashtech.modules.user.internal.patch.UserPatcher;
import com.ecommerce.nashtech.modules.user.internal.repository.UserRepository;
import com.ecommerce.nashtech.modules.user.internal.types.RawAddress;
import com.ecommerce.nashtech.modules.user.internal.types.RawPhoneNumber;
import com.ecommerce.nashtech.modules.user.internal.validation.AddressValidation;
import com.ecommerce.nashtech.modules.user.internal.validation.PhoneValidation;
import com.ecommerce.nashtech.modules.user.model.User;
import com.ecommerce.nashtech.shared.enums.UserFinder;
import com.ecommerce.nashtech.shared.types.Option;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class UserService implements IUserService {
    UserRepository userRepo;
    AccountService accountService;
    RoleService roleService;
    PhoneValidation phoneValidator;
    AddressValidation addressValidator;
    UserPatcher userPatcher;
    TransactionalOperator txOperator;
    R2dbcEntityTemplate template;

    @Override
    public Mono<User> find(UserFinder finder) {
        var user = switch (finder) {
            case UserFinder.ById id -> userRepo.findById(id.id());
            case UserFinder.ByUsername username -> userRepo.findByUsername(username.username());
            case UserFinder.ByEmail email -> userRepo.findByEmail(email.email());
            case UserFinder.ByUuid uuid -> userRepo.findByUuid(uuid.uuid());
        };
        return user.filter(u -> !u.isDeleted())
                .switchIfEmpty(Mono.error(UserError.UserNotFoundError.build(Option.none())));
    }

    @Override
    public Mono<FullUserDto> findFullUser(UserFinder finder) {
        return find(finder).flatMap(user -> {
            return accountService.findFullAccount(finder)
                    .map(account -> new FullUserDto(account.uuid(), account.username(), user.getFullName(),
                            account.email(), user.getGender(), user.getPhoneNumber(), user.getAddress(),
                            account.roles().stream().map(role -> role.getName()).collect(Collectors.toSet()),
                            user.getCreatedAtInRfc1123(), user.getUpdatedAtInRfc1123(), user.isDeleted()));
        });
    }

    @Override
    public Mono<User> create(CreateUserDto dto) {
        var rawPhoneNumber = new RawPhoneNumber(dto.phoneNumber(), dto.phoneNumberRegionCode());
        Mono<String> phoneNumber = phoneValidator.apply(rawPhoneNumber).toMono();
        var rawAddress = new RawAddress(dto.address(), dto.city(), dto.state(), dto.country());
        Mono<String> address = addressValidator.apply(rawAddress);
        return Mono.zip(phoneNumber, address).flatMap(tuple -> {
            var phoneNum = tuple.getT1();
            var addr = tuple.getT2();
            var accountDto = dto.toAccountDto();
            return accountService.create(accountDto).map(account -> {
                return User.builder()
                        .id(account.getId())
                        .phoneNumber(phoneNum)
                        .address(addr)
                        .firstName(dto.firstName())
                        .lastName(dto.lastName())
                        .middleName(dto.middleName())
                        .gender(dto.gender())
                        .createdAt(Instant.now().toEpochMilli())
                        .updatedAt(Instant.now().toEpochMilli())
                        .deleted(false)
                        .build();
            }).flatMap(u -> template.insert(User.class).using(u)).as(txOperator::transactional);
        });
    }

    @Override
    public Mono<User> unsafeCreate(CreateUserDto dto) {
        return accountService.create(dto.toAccountDto()).map(account -> {

            roleService.assignDefaultRole(account.getId());
            return User.builder()
                    .id(account.getId())
                    .phoneNumber(dto.phoneNumber())
                    .address(dto.address())
                    .firstName(dto.firstName())
                    .lastName(dto.lastName())
                    .middleName(dto.middleName())
                    .gender(dto.gender())
                    .createdAt(Instant.now().toEpochMilli())
                    .updatedAt(Instant.now().toEpochMilli())
                    .deleted(false)
                    .build();
        }).flatMap(u -> template.insert(User.class).using(u)).as(txOperator::transactional);
    }

    @Override
    public Mono<User> update(UserFinder finder, UpdateUserDto updates) {
        return find(finder).flatMap(user -> {
            Mono<String> phoneNumber = (updates.phoneNumber() != null && !updates.phoneNumber().isBlank() &&
                    updates.phoneNumberRegionCode() != null && !updates.phoneNumberRegionCode().isBlank())
                            ? phoneValidator.apply(new RawPhoneNumber(
                                    updates.phoneNumber(), updates.phoneNumberRegionCode())).toMono()
                            : Mono.just(user.getPhoneNumber());

            boolean hasAddressUpdate = Stream.of(
                    updates.address(), updates.city(), updates.state(), updates.country())
                    .anyMatch(s -> s != null && !(s instanceof String str && str.isBlank()));

            Mono<String> address = hasAddressUpdate
                    ? addressValidator.apply(new RawAddress(
                            updates.address(), updates.city(),
                            updates.state(), updates.country()))
                    : Mono.just(user.getAddress());

            return Mono.zip(phoneNumber, address).flatMap(tuple -> {
                var phoneNum = tuple.getT1();
                var addr = tuple.getT2();
                return userPatcher.patch(user, updates).toMono().doOnSuccess(u -> {
                    u.setPhoneNumber(phoneNum);
                    u.setAddress(addr);
                    u.setUpdatedAt(Instant.now().toEpochMilli());
                });
            });
        }).flatMap(user -> {
            if (Stream.of(updates.username(), updates.email(), updates.password())
                    .anyMatch(v -> v != null && !(v instanceof String str && str.isBlank()))) {
                // Ensure the result of accountService.update() is returned
                return accountService.update(finder, updates.toUpdateAccountDto())
                        .then(template.update(user)); // use .then to continue after account update
            }
            return template.update(user);
        }).as(txOperator::transactional);
    }

    @Override
    public Mono<Void> delete(UserFinder finder) {
        return find(finder).flatMap(user -> Mono.when(accountService.delete(finder),
                userRepo.setDeletedById(user.getId()))).as(txOperator::transactional)
                .then();
    }

    public Mono<String> getFullNameByUuid(UUID uuid) {
        return userRepo.findByUuid(uuid)
                .map(user -> user.getFullName())
                .switchIfEmpty(Mono.error(UserError.UserNotFoundError.build(Option.none())));
    }

}
