-- Create profiles table with CHECK constraint for gender values
CREATE TABLE IF NOT EXISTS profiles (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    date_of_birth DATE,
    bio VARCHAR(500) DEFAULT '',
    profile_picture_url VARCHAR(255),
    gender VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT check_gender CHECK (gender IN ('MALE', 'FEMALE', 'PREFER_NOT_SAY', 'OTHER'))
);

ALTER TABLE users
ADD COLUMN IF NOT EXISTS profile_id VARCHAR(36),
ADD CONSTRAINT fk_users_profile_id FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE;  

-- Create indexes for better query performance
CREATE INDEX idx_profiles_user_id ON profiles(user_id);
CREATE INDEX idx_profiles_first_name ON profiles(first_name);
CREATE INDEX idx_profiles_last_name ON profiles(last_name);
CREATE INDEX idx_profiles_date_of_birth ON profiles(date_of_birth);
