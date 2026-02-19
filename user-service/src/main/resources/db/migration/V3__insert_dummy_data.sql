-- Insert dummy users for testing purposes
INSERT INTO users (id, email, phone_number, hashed_password, role, status, created_at, updated_at)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'john.doe@example.com', '+34612345678', '$2a$10$K.fmjyC0GXHbF6k8aF6JF.E..zzT5k6Pz6q5K6H5K6H5K6H5K6H5K6', 'CUSTOMER', 'ACTIVE', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440002', 'jane.smith@example.com', '+34698765432', '$2a$10$K.fmjyC0GXHbF6k8aF6JF.E..zzT5k6Pz6q5K6H5K6H5K6H5K6H5K6', 'CUSTOMER', 'ACTIVE', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440003', 'admin.user@example.com', '+34655555555', '$2a$10$K.fmjyC0GXHbF6k8aF6JF.E..zzT5k6Pz6q5K6H5K6H5K6H5K6H5K6', 'ADMIN', 'ACTIVE', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440004', 'employee.user@example.com', '+34677777777', '$2a$10$K.fmjyC0GXHbF6k8aF6JF.E..zzT5k6Pz6q5K6H5K6H5K6H5K6H5K6', 'EMPLOYEE', 'ACTIVE', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440005', 'suspended.user@example.com', '+34688888888', '$2a$10$K.fmjyC0GXHbF6k8aF6JF.E..zzT5k6Pz6q5K6H5K6H5K6H5K6H5K6', 'CUSTOMER', 'SUSPENDED', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440006', 'pending.user@example.com', '+34699999999', '$2a$10$K.fmjyC0GXHbF6k8aF6JF.E..zzT5k6Pz6q5K6H5K6H5K6H5K6H5K6', 'CUSTOMER', 'PENDING', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440007', 'inactive.user@example.com', '+34644444444', '$2a$10$K.fmjyC0GXHbF6k8aF6JF.E..zzT5k6Pz6q5K6H5K6H5K6H5K6H5K6', 'CUSTOMER', 'INACTIVE', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440008', 'carlos.garcia@example.com', '+34611111111', '$2a$10$K.fmjyC0GXHbF6k8aF6JF.E..zzT5k6Pz6q5K6H5K6H5K6H5K6H5K6', 'CUSTOMER', 'ACTIVE', NOW(), NOW());

-- Insert dummy profiles associated with the users
INSERT INTO profiles (id, user_id, first_name, last_name, date_of_birth, bio, profile_picture_url, gender, created_at, updated_at)
VALUES
    ('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'John', 'Doe', '1990-05-15', 'Software engineer passionate about clean code and best practices.', 'https://api.example.com/profiles/1/picture.jpg', 'MALE', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Jane', 'Smith', '1988-08-22', 'Product manager with expertise in e-commerce platforms.', 'https://api.example.com/profiles/2/picture.jpg', 'FEMALE', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'Admin', 'User', '1985-12-01', 'System administrator overseeing the platform operations.', 'https://api.example.com/profiles/3/picture.jpg', 'MALE', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', 'Employee', 'Store', '1992-03-10', 'Store employee helping customers with their pharmacy needs.', 'https://api.example.com/profiles/4/picture.jpg', 'FEMALE', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440005', 'Suspended', 'Account', '1995-07-18', 'This account is currently suspended.', NULL, 'PREFER_NOT_SAY', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440006', 'Pending', 'User', '1991-09-25', 'Waiting for email verification to activate the account.', NULL, 'OTHER', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440007', 'Inactive', 'Account', '1989-11-30', 'Account marked as inactive for business reasons.', NULL, 'MALE', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440008', 'Carlos', 'García', '1993-02-14', 'Regular customer from Madrid interested in healthcare products.', 'https://api.example.com/profiles/8/picture.jpg', 'MALE', NOW(), NOW());

