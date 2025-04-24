package com.ecommerce.nashtech.modules.account.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name="accounts")
@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Account {
    
    @Id
    @Column("id")
    long id;

    @Column("uuid")
    UUID uuid;

    @Column("username")
    String username;

    @Column("password")
    String password;

    @Column("email")
    String email;

    @Column("is_deleted")
    boolean deleted;
}
