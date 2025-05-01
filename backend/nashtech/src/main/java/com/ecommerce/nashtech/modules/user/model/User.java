package com.ecommerce.nashtech.modules.user.model;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@Table(name = "users")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class User {
    @Id
    @Column("id")
    long id;

    @Column("first_name")
    String firstName;

    @Column("last_name")
    String lastName;

    @Column("middle_name")
    String middleName;

    @Column("gender")
    String gender;

    @Column("phone_number")
    String phoneNumber;

    @Column("address")
    String address;

    @Column("created_at")
    long createdAt;

    @Column("updated_at")
    long updatedAt;

    @Column("is_deleted")
    boolean deleted;

    public String getFullName() {
        return Stream.of(firstName, middleName, lastName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    public String getCreatedAtInRfc1123() {
        return toRfc1123(createdAt);
    }

    public String getUpdatedAtInRfc1123() {
        return toRfc1123(updatedAt);
    }

    private String toRfc1123(long unixTimeStamp) {
        return Instant.ofEpochMilli(unixTimeStamp)
                .atZone(java.time.ZoneId.of("GMT"))
                .format(java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME);
    }

}
