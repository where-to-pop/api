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
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,

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