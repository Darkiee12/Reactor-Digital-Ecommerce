package com.ecommerce.nashtech.modules.account.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @Column("id")
    private long id;

    @Column("name")
    private String name;
}
