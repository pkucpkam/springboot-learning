-- Tạo database
CREATE DATABASE IF NOT EXISTS customer_management_v3 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE customer_management_v3;

-- USERS
CREATE TABLE IF NOT EXISTS users (
  id            CHAR(36)     NOT NULL,        -- UUID string
  email         VARCHAR(255) NOT NULL UNIQUE,
  name          VARCHAR(255),
  picture       VARCHAR(512),
  google_sub    VARCHAR(255) UNIQUE,          -- OIDC subject (sub) from Google
  status        VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

-- ROLES
CREATE TABLE IF NOT EXISTS roles (
  id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  code  VARCHAR(64) NOT NULL UNIQUE
);

-- USERS_ROLES
CREATE TABLE IF NOT EXISTS users_roles (
  user_id CHAR(36) NOT NULL,
  role_id BIGINT   NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_users_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- CUSTOMERS
CREATE TABLE IF NOT EXISTS customers (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(255) NOT NULL,
  email      VARCHAR(255) NOT NULL UNIQUE,
  phone      VARCHAR(32),
  address	 VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- OAUTH_ACCOUNTS
CREATE TABLE IF NOT EXISTS oauth_accounts (
  id                BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id           CHAR(36) NOT NULL,
  provider          VARCHAR(32)  NOT NULL,            -- 'google'
  provider_user_id  VARCHAR(255) NOT NULL,            -- google sub
  email             VARCHAR(255),
  name              VARCHAR(255),
  picture           VARCHAR(512),
  scopes            TEXT,
  raw_info          JSON,
  created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_provider_user (provider, provider_user_id),
  CONSTRAINT fk_oauth_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- REFRESH_TOKENS (điều chỉnh đúng theo entity)
-- Fields: id, userId, tokenHash, expiresAt, revoked, parentTokenHash, createdAt
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id            CHAR(36)     NOT NULL,
  session_id         CHAR(36)     NOT NULL UNIQUE,   -- session/opaque ID (UUID)
  token_hash         VARCHAR(88)  NOT NULL UNIQUE,  -- Base64(SHA-256) ~44 chars; 88 để dư phòng
  expires_at         TIMESTAMP    NOT NULL,
  revoked            BOOLEAN      NOT NULL DEFAULT FALSE,
  parent_token_hash  VARCHAR(88)  NULL,
  created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Roles
INSERT INTO roles(code) VALUES ('ROLE_USER'), ('ROLE_ADMIN')
ON DUPLICATE KEY UPDATE code = VALUES(code);

-- Users
INSERT INTO users (id, email, name, picture, google_sub, status)
VALUES
  (UUID(), 'alice@example.com', 'Alice', NULL, NULL, 'ACTIVE'),
  (UUID(), 'bob@example.com',   'Bob',   NULL, NULL, 'ACTIVE');

-- Lấy id
SET @alice_id = (SELECT id FROM users WHERE email='alice@example.com' LIMIT 1);
SET @bob_id   = (SELECT id FROM users WHERE email='bob@example.com' LIMIT 1);

-- Gán roles
INSERT INTO users_roles(user_id, role_id)
VALUES
  (@alice_id, (SELECT id FROM roles WHERE code='ROLE_ADMIN')),
  (@bob_id,   (SELECT id FROM roles WHERE code='ROLE_USER'))
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id), role_id = VALUES(role_id);

-- Customers
INSERT INTO customers(name, email, phone, address)
VALUES
  ('Charlie', 'charlie@company.com', '0901234567', 'Hà Nội'),
  ('Daisy',   'daisy@company.com',   '0912345678', 'Hồ Chí Minh')
ON DUPLICATE KEY UPDATE name = VALUES(name), phone = VALUES(phone), address = VALUES(address);

-- OAuth accounts (Google)
INSERT INTO oauth_accounts(user_id, provider, provider_user_id, email, name, picture, scopes, raw_info)
VALUES
  (@alice_id, 'google', 'google-sub-123', 'alice@example.com', 'Alice', NULL, 'openid email profile', JSON_OBJECT('locale', 'en')),
  (@bob_id,   'google', 'google-sub-456', 'bob@example.com',   'Bob',   NULL, 'openid email profile', JSON_OBJECT('locale', 'vi'))
ON DUPLICATE KEY UPDATE email = VALUES(email), name = VALUES(name), picture = VALUES(picture);

-- Refresh tokens (hash mẫu; trong thực tế là Base64(SHA-256(plain)))
-- Refresh tokens (hash mẫu; trong thực tế là Base64(SHA-256(plain)))
INSERT INTO refresh_tokens(user_id, session_id, token_hash, expires_at, revoked, parent_token_hash, created_at)
VALUES
  (@alice_id, UUID(), 'hash_alice_token_abc_base64', DATE_ADD(NOW(), INTERVAL 14 DAY), FALSE, NULL, NOW()),
  (@bob_id,   UUID(), 'hash_bob_token_xyz_base64',   DATE_ADD(NOW(), INTERVAL 14 DAY), FALSE, NULL, NOW());

