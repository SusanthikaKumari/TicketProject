INSERT INTO users (
    first_name,
    last_name,
    email,
    password,
    role_id,
    department_id,
    enabled,
    created_at,
    updated_at
)
SELECT
    'System',
    'Administrator',
    'administrator@gmail.com',
    '$2a$10$aeXqWnHdv50gsc6usQIupu/7iMd8mOJn0eW8y9rnzOOsLHwC7PBg.',
    role_id,
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM roles
WHERE role_name = 'ADMINISTRATOR';
