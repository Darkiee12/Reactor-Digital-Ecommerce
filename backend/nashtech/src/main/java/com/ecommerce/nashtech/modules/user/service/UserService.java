package com.ecommerce.nashtech.modules.user.service;

import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.modules.account.service.AccountService;
import com.ecommerce.nashtech.modules.user.dto.CreateUserDto;
import com.ecommerce.nashtech.modules.user.internal.repository.UserRepository;
import com.ecommerce.nashtech.modules.user.internal.types.RawAddress;
import com.ecommerce.nashtech.modules.user.internal.types.RawPhoneNumber;
import com.ecommerce.nashtech.modules.user.internal.validation.AddressValidation;
import com.ecommerce.nashtech.modules.user.internal.validation.PhoneValidation;
import com.ecommerce.nashtech.modules.user.model.User;
import com.ecommerce.nashtech.shared.enums.UserFinder;

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
                        user.setPhoneNumber(phoneNum);
                        user.setAddress(addr);
                        return user;
                    })
                    .flatMap(userRepo::save);
            });
    }

    @Override
    public Mono<User> find(UserFinder finder) {
        return switch (finder) {
            case UserFinder.ById id -> userRepo.findById(id.id());
            case UserFinder.ByUsername username -> userRepo.findByUsername(username.username());
            case UserFinder.ByEmail email -> userRepo.findByEmail(email.email());
            case UserFinder.ByUuid uuid -> userRepo.findByUuid(uuid.uuid());
        };
    }

    @Override
    public Mono<User> update(CreateUserDto dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Mono<Void> delete(UserFinder finder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
