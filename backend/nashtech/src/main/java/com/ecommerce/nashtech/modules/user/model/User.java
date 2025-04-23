package com.ecommerce.nashtech.modules.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @Column("id")
    private long id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("middle_name")
    private String middleName;

    @Column("gender")
    private String gender;

    @Column("phone_number")
    private String phoneNumber;

    @Column("address")
    private String address;

    @Column("created_at")
    private long createdAt;

    @Column("updated_at")
    private long updatedAt;

}
