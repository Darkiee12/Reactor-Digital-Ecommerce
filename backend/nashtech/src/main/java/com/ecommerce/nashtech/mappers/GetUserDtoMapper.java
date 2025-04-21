package com.ecommerce.nashtech.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.dto.UserDto;
import com.ecommerce.nashtech.models.User;

@Service
public class GetUserDtoMapper implements Function<User, UserDto> {
    @Override
    public UserDto apply(User user) {
        var account = user.getAccount();
        var fullName = getFullName(user.getFirstName(), user.getMiddleName(), user.getLastName());
        return new UserDto(
            account.getUuid(),
            account.getUsername(),
            fullName,
            account.getEmail(),
            user.getGender(),
            user.getPhoneNumber(),
            user.getAddress(),
            account.getRoles(),
            account.getCreatedAt()        
        );
    }

    private String getFullName(String firstName, String middleName, String lastName) {
        List<String> fullName = new ArrayList<>();
        if (firstName != null && !firstName.isEmpty()) {
            fullName.add(firstName);
        }
        if (middleName != null && !middleName.isEmpty()) {
            fullName.add(middleName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            fullName.add(lastName);
        }
        return fullName.stream().reduce((a, b) -> a + " " + b).orElse("");
    }
    
}
