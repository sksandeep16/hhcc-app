-- =============================================
-- DDL Script for SQLite (jdbc:sqlite:demo)
-- =============================================

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    username        TEXT    NOT NULL UNIQUE,
    email           TEXT    NOT NULL UNIQUE,
    password        TEXT    NOT NULL,
    role            TEXT    NOT NULL DEFAULT 'USER'
);

-- Registration Table
CREATE TABLE IF NOT EXISTS registration (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER NOT NULL UNIQUE,
    registered_at   TEXT    NOT NULL DEFAULT (datetime('now')),
    status          TEXT    NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Pets Table
CREATE TABLE IF NOT EXISTS pets (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER NOT NULL,
    name            TEXT    NOT NULL,
    species         TEXT    NOT NULL,
    breed           TEXT,
    date_of_birth   TEXT,
    gender          TEXT    NOT NULL DEFAULT 'Unknown',
    CONSTRAINT fk_pet_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Feedback Table
CREATE TABLE IF NOT EXISTS feedback (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id      INTEGER,
    name         TEXT    NOT NULL,
    email        TEXT    NOT NULL,
    category     TEXT    NOT NULL DEFAULT 'FEEDBACK',
    support_type TEXT,
    rating       INTEGER,
    message      TEXT    NOT NULL,
    status       TEXT    NOT NULL DEFAULT 'OPEN',
    created_at   TEXT    NOT NULL DEFAULT (datetime('now')),
    CONSTRAINT fk_fb_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Care Services Table
CREATE TABLE IF NOT EXISTS care_services (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT    NOT NULL,
    icon        TEXT    NOT NULL DEFAULT '',
    description TEXT    NOT NULL,
    image_url   TEXT,
    sort_order  INTEGER NOT NULL DEFAULT 0
);

-- Service Bullets Table
CREATE TABLE IF NOT EXISTS service_bullets (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    service_id  INTEGER NOT NULL,
    bullet_text TEXT    NOT NULL,
    sort_order  INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_bullet_service FOREIGN KEY (service_id) REFERENCES care_services(id) ON DELETE CASCADE
);

-- Family Members Table
CREATE TABLE IF NOT EXISTS family_members (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER NOT NULL,
    first_name      TEXT    NOT NULL,
    last_name       TEXT    NOT NULL,
    relationship    TEXT    NOT NULL,
    date_of_birth   TEXT,
    CONSTRAINT fk_family_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id      INTEGER NOT NULL,
    amount       REAL    NOT NULL,
    method       TEXT    NOT NULL,
    status       TEXT    NOT NULL DEFAULT 'SUCCESS',
    card_last4   TEXT,
    receipt_url  TEXT,
    transaction_id TEXT,
    created_at   TEXT    NOT NULL DEFAULT (datetime('now')),
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
