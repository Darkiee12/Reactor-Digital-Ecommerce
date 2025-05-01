CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE account_role (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at BIGINT NOT NULL,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT uq_account_role_accountid_roleid UNIQUE (account_id, role_id)
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY REFERENCES accounts(id),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    middle_name VARCHAR(255),
    gender VARCHAR(50),
    phone_number VARCHAR(20),
    address TEXT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);
