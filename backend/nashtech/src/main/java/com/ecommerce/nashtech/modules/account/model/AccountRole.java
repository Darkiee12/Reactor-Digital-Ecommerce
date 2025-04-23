package com.ecommerce.nashtech.modules.account.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Table(name="account_role")
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)

public class AccountRole {
    @Id
    @Column("id")
    long id;
    @Column("account_id")
    long accountId;
    @Column("role_id")
    long roleId;
    @Column("assigned_at")
    long assignedAt;
}
