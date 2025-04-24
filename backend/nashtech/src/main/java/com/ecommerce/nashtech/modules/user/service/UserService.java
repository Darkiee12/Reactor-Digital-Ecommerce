package com.ecommerce.nashtech.modules.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.ecommerce.nashtech.modules.account.service.AccountService;
import com.ecommerce.nashtech.modules.user.dto.CreateUserDto;
import com.ecommerce.nashtech.modules.user.dto.UpdateUserDto;
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
    public Mono<User> create(CreateUserDto dto) {
        var rawPhoneNumber = new RawPhoneNumber(dto.phoneNumber(), dto.phoneNumberRegionCode());
        Mono<String> phoneNumber = phoneValidator.apply(rawPhoneNumber).toMono();
        var rawAddress = new RawAddress(dto.address(), dto.city(), dto.state(), dto.country());
        Mono<String> address = addressValidator.apply(rawAddress);
        return Mono.zip(phoneNumber, address)
            .flatMap(tuple -> {
                var phoneNum = tuple.getT1();
                var addr = tuple.getT2();
                var user = new User();
                var accountDto = dto.toAccountDto();
                return accountService.create(accountDto)
                    .map(account -> {
                        user.setId(account.getId());
                        user.setPhoneNumber(phoneNum);
                        user.setAddress(addr);
                        user.setFirstName(dto.firstName());
                        user.setLastName(dto.lastName());
                        user.setMiddleName(dto.middleName());
                        user.setGender(dto.gender());
                        user.setCreatedAt(Instant.now().toEpochMilli());
                        user.setUpdatedAt(Instant.now().toEpochMilli());
                        user.setDeleted(false);
                        return user;
                    })
                    .flatMap(u -> template.insert(User.class).using(u))
                    .as(txOperator::transactional);
            });
    }


    @Override
    public Mono<User> update(UserFinder finder, UpdateUserDto updates) {
        return find(finder)
            .flatMap(user -> {
                var rawPhoneNumber = new RawPhoneNumber(updates.phoneNumber(), updates.phoneNumberRegionCode());
                Mono<String> phoneNumber = (updates.phoneNumber() != null && updates.phoneNumberRegionCode() != null)
                    ? phoneValidator.apply(rawPhoneNumber).toMono() 
                    : Mono.just(user.getPhoneNumber());

                var rawAddress = new RawAddress(updates.address(), updates.city(), updates.state(), updates.country());
                Mono<String> address = (updates.address() != null || updates.city() != null || updates.state() != null || updates.country() != null)
                    ? addressValidator.apply(rawAddress)
                    : Mono.just(user.getAddress());

                return Mono.zip(phoneNumber, address)
                    .flatMap(tuple -> {
                        var phoneNum = tuple.getT1();
                        var addr = tuple.getT2();
                        return userPatcher.patch(user, updates)
                            .toMono()
                            .doOnSuccess(u -> {
                                u.setPhoneNumber(phoneNum);
                                u.setAddress(addr);
                                u.setUpdatedAt(Instant.now().toEpochMilli());
                            });
                        
                    });
            })
            .flatMap(user -> userRepo.save(user))
            .as(txOperator::transactional);
    }

    @Override
    public Mono<Void> delete(UserFinder finder) {
        find(finder)
            .flatMap(user -> userRepo.setDeletedById(user.getId()))
            .as(txOperator::transactional);
        return Mono.empty();
    }
}
