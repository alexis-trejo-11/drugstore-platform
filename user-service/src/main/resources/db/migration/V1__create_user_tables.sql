CREATE TABLE users (
    -- Account information
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20) UNIQUE,
    hashed_password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED')),
    role VARCHAR(20) NOT NULL CHECK (role IN ('CUSTOMER', 'ADMIN', 'EMPLOYEE', 'GUEST')),
    two_factor_id VARCHAR(255),
    profile_id UUID UNIQUE,
    
    -- Timestamps
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints adicionales
    CONSTRAINT valid_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT valid_phone CHECK (phone_number ~* '^\+?[1-9]\d{1,14}$' OR phone_number IS NULL)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone_number ON users(phone_number);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_role ON users(role);


COMMENT ON TABLE users IS 'Tabla principal de usuarios del sistema';
COMMENT ON COLUMN users.two_factor_id IS 'Referencia al ID de configuración 2FA (null si no está habilitado)';
COMMENT ON COLUMN users.status IS 'Estado del usuario: PENDING, ACTIVE, INACTIVE, SUSPENDED, DELETED';
COMMENT ON COLUMN users.role IS 'Rol del usuario: USER, ADMIN, MODERATOR';