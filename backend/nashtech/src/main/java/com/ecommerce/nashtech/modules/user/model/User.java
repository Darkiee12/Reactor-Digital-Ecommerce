package com.ecommerce.nashtech.modules.user.model;

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

}
