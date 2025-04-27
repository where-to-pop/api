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
CREATE TABLE IF NOT EXISTS area_population (
    id BIGINT PRIMARY KEY,
    area_id BIGINT NOT NULL,
    area_name VARCHAR(255) NOT NULL,
    area_code VARCHAR(255) NOT NULL,
    congestion_level INT NOT NULL,
    congestion_message VARCHAR(255) NOT NULL,
    ppltn_min INT NOT NULL,
    ppltn_max INT NOT NULL,
    male_ppltn_rate DOUBLE NOT NULL,
    female_ppltn_rate DOUBLE NOT NULL,
    ppltn_rate_0 DOUBLE NOT NULL,
    ppltn_rate_10 DOUBLE NOT NULL,
    ppltn_rate_20 DOUBLE NOT NULL,
    ppltn_rate_30 DOUBLE NOT NULL,
    ppltn_rate_40 DOUBLE NOT NULL,
    ppltn_rate_50 DOUBLE NOT NULL,
    ppltn_rate_60 DOUBLE NOT NULL,
    ppltn_rate_70 DOUBLE NOT NULL,
    resnt_ppltn_rate DOUBLE NOT NULL,
    non_resnt_ppltn_rate DOUBLE NOT NULL,
    replace_yn BOOLEAN NOT NULL,
    ppltn_time TIMESTAMP(6) NOT NULL,
    fcst_yn BOOLEAN NOT NULL,
    fcst_time TIMESTAMP(6) DEFAULT NULL,
    fcst_congestion_level INT DEFAULT NULL,
    fcst_ppltn_min INT DEFAULT NULL,
    fcst_ppltn_max INT DEFAULT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) DEFAULT NULL,

    -- FK 제약
    CONSTRAINT fk_area_population_area FOREIGN KEY (area_id) REFERENCES areas(id) ON DELETE CASCADE,

    -- 인덱스
    INDEX idx_area_population_area_id (area_id),
    INDEX idx_area_population_area_code (area_code),
    INDEX idx_area_population_ppltn_time (ppltn_time),
    INDEX idx_area_population_fcst_time (fcst_time),
    INDEX idx_area_population_deleted_at (deleted_at)
    );
