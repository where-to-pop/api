-- areas 테이블 생성
CREATE TABLE IF NOT EXISTS areas (
                                     id BIGINT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,

    -- 인덱스
    INDEX idx_areas_name (name),
    INDEX idx_areas_deleted_at (deleted_at)
    );

-- area_population 테이블 생성
CREATE TABLE IF NOT EXISTS area_populations (
    id BIGINT PRIMARY KEY,
    area_id BIGINT NOT NULL,
    area_name VARCHAR(255) NOT NULL,
    area_code VARCHAR(255) NOT NULL,
    congestion_level VARCHAR(255) NOT NULL,
    congestion_message VARCHAR(255) NOT NULL,
    population_min INT NOT NULL,
    population_max INT NOT NULL,
    male_population_rate DOUBLE NOT NULL,
    female_population_rate DOUBLE NOT NULL,
    population_rate_0 DOUBLE NOT NULL,
    population_rate_10 DOUBLE NOT NULL,
    population_rate_20 DOUBLE NOT NULL,
    population_rate_30 DOUBLE NOT NULL,
    population_rate_40 DOUBLE NOT NULL,
    population_rate_50 DOUBLE NOT NULL,
    population_rate_60 DOUBLE NOT NULL,
    population_rate_70 DOUBLE NOT NULL,
    resident_population_rate DOUBLE NOT NULL,
    non_resident_population_rate DOUBLE NOT NULL,
    replace_yn BOOLEAN NOT NULL,
    population_update_time TIMESTAMP(6) NOT NULL,
    forecast_yn BOOLEAN NOT NULL,
    forecast_population_json TEXT DEFAULT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,

    -- FK 제약
    CONSTRAINT fk_area_population_area FOREIGN KEY (area_id) REFERENCES areas(id) ON DELETE CASCADE,

    -- 인덱스
    INDEX idx_area_population_area_id (area_id),
    INDEX idx_area_population_area_code (area_code),
    INDEX idx_area_population_update_time (population_update_time),
    INDEX idx_area_population_deleted_at (deleted_at)
    );

-- popups 테이블 생성
CREATE TABLE IF NOT EXISTS popups (
    id BIGINT PRIMARY KEY,
    building_id BIGINT,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,

    CONSTRAINT fk_popup_building
        FOREIGN KEY (building_id)
        REFERENCES buildings(id)
        ON DELETE CASCADE,

    -- 검색 성능 향상을 위한 인덱스
    INDEX idx_popups_name (name),
    INDEX idx_popups_deleted_at (deleted_at)
);

-- popup_popply 테이블 생성
CREATE TABLE IF NOT EXISTS popup_popply (
    id BIGINT PRIMARY KEY,
    popup_id BIGINT NOT NULL,
    popup_name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    optional_address VARCHAR(500) DEFAULT NULL,
    start_date TIMESTAMP(6) DEFAULT NULL,
    end_date TIMESTAMP(6) DEFAULT NULL,
    description TEXT NOT NULL,
    url VARCHAR(2048) DEFAULT NULL,
    latitude DOUBLE PRECISION DEFAULT NULL,
    longitude DOUBLE PRECISION DEFAULT NULL,
    organizer_name VARCHAR(255) DEFAULT NULL,
    organizer_url VARCHAR(2048) DEFAULT NULL,
    popply_id INT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    deleted_at TIMESTAMP(6) DEFAULT NULL,

    CONSTRAINT fk_popup_popply_popup
        FOREIGN KEY (popup_id)
        REFERENCES popups(id)
        ON DELETE CASCADE,

    INDEX idx_popup_popply_popup_id (popup_id),
    INDEX idx_popup_popply_dates (start_date, end_date),
    INDEX idx_popup_popply_popply_id (popply_id),
    INDEX idx_popup_popply_deleted_at (deleted_at)
);

-- popup_x 테이블 생성
CREATE TABLE IF NOT EXISTS popup_x (
    id BIGINT PRIMARY KEY,
    popup_id BIGINT NOT NULL,
    written_at DATETIME(6) NOT NULL,
    content TEXT NOT NULL,
    emotion_score ENUM('VERY_GOOD', 'GOOD', 'NEUTRAL', 'BAD', 'VERY_BAD') NOT NULL,
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT fk_popup_x_popup
        FOREIGN KEY (popup_id)
        REFERENCES popups(id)
        ON DELETE CASCADE,

    INDEX idx_popup_x_popup_id (popup_id)
);

-- building table 생성
CREATE TABLE IF NOT EXISTS buildings (
    id BIGINT PRIMARY KEY,
    address VARCHAR(500) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,

    INDEX idx_buildings_address (address)
);

-- building register table 생성
CREATE TABLE IF NOT EXISTS building_registers (
    id BIGINT PRIMARY KEY,
    building_id BIGINT NOT NULL,
    address VARCHAR(500) NOT NULL,
    heit DOUBLE PRECISION DEFAULT NULL,
    grnd_flr_cnt INT DEFAULT NULL,
    ugrnd_flr_cnt INT DEFAULT NULL,
    ride_use_elvt_cnt INT DEFAULT NULL,
    emgen_use_elvt_cnt INT DEFAULT NULL,
    use_apr_day TIMESTAMP(6) DEFAULT NULL,
    bld_nm VARCHAR(255) DEFAULT NULL,
    plat_area DOUBLE PRECISION DEFAULT NULL,
    arch_area DOUBLE PRECISION DEFAULT NULL,
    bc_rat DOUBLE PRECISION DEFAULT NULL,
    val_rat DOUBLE PRECISION DEFAULT NULL,
    tot_area DOUBLE PRECISION DEFAULT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,

    CONSTRAINT fk_building_building_registers
        FOREIGN KEY (building_id)
        REFERENCES buildings(id)
        ON DELETE CASCADE
);

-- 사용자 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(2048) DEFAULT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,
    
    -- 유니크 제약
    UNIQUE (email),
    
    -- 인덱스
    INDEX idx_users_username (username),
    INDEX idx_users_email (email),
    INDEX idx_users_deleted_at (deleted_at)
);

-- 인증 사용자 테이블 생성
CREATE TABLE IF NOT EXISTS auth_users (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    identifier VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,
    
    -- FK 제약
    CONSTRAINT fk_auth_users_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    
    -- 유니크 제약
    UNIQUE (identifier),
    
    -- 인덱스
    INDEX idx_auth_users_user_id (user_id),
    INDEX idx_auth_users_identifier (identifier),
    INDEX idx_auth_users_deleted_at (deleted_at)
);

-- 리프레시 토큰 테이블 생성
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT PRIMARY KEY,
    auth_user_id BIGINT NOT NULL,
    token VARCHAR(512) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,
    
    -- FK 제약
    CONSTRAINT fk_refresh_tokens_auth_user
        FOREIGN KEY (auth_user_id)
        REFERENCES auth_users(id)
        ON DELETE CASCADE,
    
    -- 유니크 제약
    UNIQUE (token),
    
    -- 인덱스
    INDEX idx_refresh_tokens_auth_user_id (auth_user_id),
    INDEX idx_refresh_tokens_token (token),
    INDEX idx_refresh_tokens_expires_at (expires_at),
    INDEX idx_refresh_tokens_deleted_at (deleted_at)
);

-- 프로젝트 테이블 생성
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    brand_name VARCHAR(255) NOT NULL,
    popup_category VARCHAR(50) NOT NULL,
    popup_type VARCHAR(50) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    primary_target_age_group VARCHAR(50) NOT NULL,
    secondary_target_age_group VARCHAR(50) DEFAULT NULL,
    brand_scale VARCHAR(50) NOT NULL,
    project_goal TEXT NOT NULL,
    additional_brand_info TEXT DEFAULT NULL,
    additional_project_info TEXT DEFAULT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,
    
    -- FK 제약
    CONSTRAINT fk_projects_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    
    -- 인덱스
    INDEX idx_projects_owner_id (owner_id),
    INDEX idx_projects_name (name),
    INDEX idx_projects_brand_name (brand_name),
    INDEX idx_projects_popup_category (popup_category),
    INDEX idx_projects_popup_type (popup_type),
    INDEX idx_projects_deleted_at (deleted_at)
);

-- 채팅 테이블 생성
CREATE TABLE IF NOT EXISTS chats (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,
    
    -- FK 제약
    CONSTRAINT fk_chats_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_chats_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,
    
    -- 인덱스
    INDEX idx_chats_user_id (user_id),
    INDEX idx_chats_project_id (project_id),
    INDEX idx_chats_title (title),
    INDEX idx_chats_is_active (is_active),
    INDEX idx_chats_deleted_at (deleted_at)
);

-- 채팅 메시지 테이블 생성
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    finish_reason VARCHAR(50) NULL,
    step_result TEXT NULL,
    latency_ms BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,
    
    -- FK 제약
    CONSTRAINT fk_chat_messages_chat
        FOREIGN KEY (chat_id)
        REFERENCES chats(id)
        ON DELETE CASCADE,
    
    -- 인덱스
    INDEX idx_chat_messages_chat_id (chat_id),
    INDEX idx_chat_messages_role (role),
    INDEX idx_chat_messages_deleted_at (deleted_at)
);
