-- ============================================================================
-- CreatorConnect — MySQL Database Schema
-- Normalized to 3NF. Every table carries: PK, FKs, created_at, updated_at,
-- and either an explicit status column or the shared is_deleted soft-delete flag.
-- Note: Hibernate (ddl-auto=update) can also generate/evolve this schema from
-- the JPA entities automatically in dev; this file is the authoritative,
-- reviewable DDL for production provisioning (Railway/PlanetScale) and for
-- anyone auditing the design without booting the app.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS creatorconnect_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE creatorconnect_db;

-- ---------------------------------------------------------------------------
-- USERS — core auth table, one row per account regardless of role
-- ---------------------------------------------------------------------------
CREATE TABLE users (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    email                VARCHAR(150) NOT NULL UNIQUE,
    password             VARCHAR(255) NOT NULL,          -- BCrypt hash
    full_name            VARCHAR(150) NOT NULL,
    phone                VARCHAR(20),
    role                 ENUM('ADMIN','COMPANY','CREATOR') NOT NULL,
    status               ENUM('PENDING_VERIFICATION','ACTIVE','SUSPENDED','DEACTIVATED') NOT NULL DEFAULT 'PENDING_VERIFICATION',
    email_verified       BOOLEAN NOT NULL DEFAULT FALSE,
    verification_token   VARCHAR(255),
    reset_password_token VARCHAR(255),
    reset_token_expiry   DATETIME,
    remember_me          BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at        DATETIME,
    is_deleted           BOOLEAN NOT NULL DEFAULT FALSE,
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- CREATORS — 1:1 extension of users where role = CREATOR
-- ---------------------------------------------------------------------------
CREATE TABLE creators (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                     BIGINT NOT NULL UNIQUE,
    profile_picture_url         VARCHAR(500),
    cover_photo_url             VARCHAR(500),
    bio                         VARCHAR(2000),
    location                    VARCHAR(150),
    languages                   VARCHAR(500),
    category                    VARCHAR(100),
    experience_years            INT,
    follower_count              BIGINT NOT NULL DEFAULT 0,
    engagement_rate             DOUBLE NOT NULL DEFAULT 0,
    instagram_handle            VARCHAR(150),
    youtube_handle               VARCHAR(150),
    linkedin_handle              VARCHAR(150),
    facebook_handle               VARCHAR(150),
    tiktok_handle                VARCHAR(150),
    portfolio_url               VARCHAR(500),
    skills                       VARCHAR(1000),
    achievements                 VARCHAR(1000),
    is_verified                  BOOLEAN NOT NULL DEFAULT FALSE,
    average_rating               DOUBLE NOT NULL DEFAULT 0,
    completed_campaigns_count    INT NOT NULL DEFAULT 0,
    success_rate                 DOUBLE NOT NULL DEFAULT 0,
    total_earnings                DECIMAL(12,2) NOT NULL DEFAULT 0,
    profile_completion_percent    INT NOT NULL DEFAULT 20,
    is_deleted                   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at                   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_creators_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_creators_category (category),
    INDEX idx_creators_followers (follower_count)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- COMPANIES — 1:1 extension of users where role = COMPANY
-- ---------------------------------------------------------------------------
CREATE TABLE companies (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT NOT NULL UNIQUE,
    company_name      VARCHAR(200) NOT NULL,
    logo_url          VARCHAR(500),
    website           VARCHAR(300),
    industry          VARCHAR(100),
    description       VARCHAR(2000),
    location          VARCHAR(150),
    instagram_handle  VARCHAR(150),
    linkedin_handle   VARCHAR(150),
    twitter_handle    VARCHAR(150),
    is_verified       BOOLEAN NOT NULL DEFAULT FALSE,
    total_campaigns   INT NOT NULL DEFAULT 0,
    total_spending    DECIMAL(12,2) NOT NULL DEFAULT 0,
    average_rating    DOUBLE NOT NULL DEFAULT 0,
    is_deleted        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_companies_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_companies_name (company_name)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- CAMPAIGNS
-- ---------------------------------------------------------------------------
CREATE TABLE campaigns (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id            BIGINT NOT NULL,
    title                 VARCHAR(255) NOT NULL,
    description           VARCHAR(4000) NOT NULL,
    budget                DECIMAL(12,2) NOT NULL,
    min_followers         BIGINT,
    preferred_platform    ENUM('INSTAGRAM','YOUTUBE','TIKTOK','FACEBOOK','LINKEDIN','TWITTER_X','BLOG','OTHER'),
    category              VARCHAR(100),
    deadline              DATE,
    banner_url            VARCHAR(500),
    guidelines            VARCHAR(4000),
    reference_files_url   VARCHAR(2000),
    deliverables          VARCHAR(2000),
    creators_required     INT NOT NULL DEFAULT 1,
    status                ENUM('DRAFT','OPEN','CLOSED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'OPEN',
    view_count            BIGINT NOT NULL DEFAULT 0,
    is_deleted            BOOLEAN NOT NULL DEFAULT FALSE,
    created_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_campaigns_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    INDEX idx_campaigns_status (status),
    INDEX idx_campaigns_category (category),
    INDEX idx_campaigns_deadline (deadline)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- APPLICATIONS
-- ---------------------------------------------------------------------------
CREATE TABLE applications (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id        BIGINT NOT NULL,
    creator_id         BIGINT NOT NULL,
    message            VARCHAR(2000),
    portfolio_link     VARCHAR(500),
    expected_timeline  VARCHAR(150),
    status             ENUM('PENDING','SHORTLISTED','ACCEPTED','REJECTED','COMPLETED','WITHDRAWN') NOT NULL DEFAULT 'PENDING',
    reviewed_at        DATETIME,
    rejection_reason   VARCHAR(1000),
    is_deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_applications_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE,
    CONSTRAINT fk_applications_creator FOREIGN KEY (creator_id) REFERENCES creators(id) ON DELETE CASCADE,
    CONSTRAINT uq_campaign_creator UNIQUE (campaign_id, creator_id),
    INDEX idx_applications_status (status)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- CONTENT_SUBMISSIONS
-- ---------------------------------------------------------------------------
CREATE TABLE content_submissions (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id         BIGINT NOT NULL UNIQUE,
    instagram_reel_link    VARCHAR(500),
    youtube_link           VARCHAR(500),
    tiktok_link            VARCHAR(500),
    drive_link             VARCHAR(500),
    comments               VARCHAR(2000),
    submission_date        DATETIME NOT NULL,
    status                 ENUM('SUBMITTED','APPROVED','REJECTED','CHANGES_REQUESTED') NOT NULL DEFAULT 'SUBMITTED',
    review_comments        VARCHAR(2000),
    reviewed_at            DATETIME,
    is_deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_submissions_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- PAYMENTS
-- ---------------------------------------------------------------------------
CREATE TABLE payments (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id    BIGINT NOT NULL,
    creator_id        BIGINT NOT NULL,
    company_id        BIGINT NOT NULL,
    amount            DECIMAL(12,2) NOT NULL,
    status            ENUM('PENDING','APPROVED','PAID','REJECTED') NOT NULL DEFAULT 'PENDING',
    invoice_number    VARCHAR(50) UNIQUE,
    paid_at           DATETIME,
    payment_notes     VARCHAR(1000),
    is_deleted        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_payments_creator FOREIGN KEY (creator_id) REFERENCES creators(id) ON DELETE CASCADE,
    CONSTRAINT fk_payments_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    INDEX idx_payments_status (status)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- NOTIFICATIONS
-- ---------------------------------------------------------------------------
CREATE TABLE notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    type        ENUM('APPLICATION_RECEIVED','APPLICATION_ACCEPTED','APPLICATION_REJECTED','APPLICATION_SHORTLISTED',
                      'SUBMISSION_UPLOADED','SUBMISSION_APPROVED','SUBMISSION_REJECTED','PAYMENT_RELEASED',
                      'CAMPAIGN_CLOSED','ACCOUNT_VERIFIED','SYSTEM_ALERT') NOT NULL,
    title       VARCHAR(255) NOT NULL,
    message     VARCHAR(1000),
    link_url    VARCHAR(500),
    is_read     BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- SAVED_CAMPAIGNS (bookmarks)
-- ---------------------------------------------------------------------------
CREATE TABLE saved_campaigns (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id  BIGINT NOT NULL,
    creator_id   BIGINT NOT NULL,
    is_deleted   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_saved_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE,
    CONSTRAINT fk_saved_creator FOREIGN KEY (creator_id) REFERENCES creators(id) ON DELETE CASCADE,
    CONSTRAINT uq_saved_campaign_creator UNIQUE (campaign_id, creator_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- REVIEWS
-- ---------------------------------------------------------------------------
CREATE TABLE reviews (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id     BIGINT NOT NULL,
    reviewer_user_id   BIGINT NOT NULL,
    reviewee_user_id   BIGINT NOT NULL,
    rating             TINYINT NOT NULL,   -- 1 to 5
    comment            VARCHAR(2000),
    is_deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_reviewee FOREIGN KEY (reviewee_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_rating_range CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- ACTIVITY_LOGS (dashboard "recent activity" feed)
-- ---------------------------------------------------------------------------
CREATE TABLE activity_logs (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    action       VARCHAR(100) NOT NULL,
    description  VARCHAR(1000),
    entity_type  VARCHAR(100),
    entity_id    BIGINT,
    is_deleted   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_activity_user (user_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- AUDIT_LOGS (security / admin action trail — independent of business entities)
-- ---------------------------------------------------------------------------
CREATE TABLE audit_logs (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_user_id  BIGINT,
    event          VARCHAR(100) NOT NULL,
    details        VARCHAR(2000),
    ip_address     VARCHAR(50),
    is_deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_audit_actor (actor_user_id)
) ENGINE=InnoDB;
