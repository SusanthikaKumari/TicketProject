CREATE TABLE roles (
    role_id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE departments (
    department_id BIGSERIAL PRIMARY KEY,
    department_name VARCHAR(200) NOT NULL UNIQUE
);


CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(200) NOT NULL,
    last_name VARCHAR(200) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    role_id BIGINT NOT NULL,
    department_id BIGINT,

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
        REFERENCES roles(role_id),

    CONSTRAINT fk_users_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
);


CREATE TABLE tickets (
    ticket_id BIGSERIAL PRIMARY KEY,
    ticket_code VARCHAR(255) UNIQUE,

    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,

    priority VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',

    customer_id BIGINT,
    assigned_admin_id BIGINT,
    department_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT fk_tickets_customer
        FOREIGN KEY (customer_id)
        REFERENCES users(user_id),

    CONSTRAINT fk_tickets_assigned_admin
        FOREIGN KEY (assigned_admin_id)
        REFERENCES users(user_id),

    CONSTRAINT fk_tickets_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
);


CREATE TABLE messages (
    message_id BIGSERIAL PRIMARY KEY,

    message TEXT,

    ticket_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT fk_messages_ticket
        FOREIGN KEY (ticket_id)
        REFERENCES tickets(ticket_id),

    CONSTRAINT fk_messages_sender
        FOREIGN KEY (sender_id)
        REFERENCES users(user_id)
);


INSERT INTO roles (role_name)
VALUES
    ('CUSTOMER'),
    ('ADMIN'),
    ('MANAGER');