-- ===================================================================
-- 2. category (중분류 카테고리 테이블 DDL)
-- 엔티티: com.honeybudget.domain.Category
-- ===================================================================

CREATE TABLE IF NOT EXISTS `category` (
    `category_id`       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '중분류 카테고리 고유 ID (PK)',
    `category_group_id` BIGINT       NOT NULL                 COMMENT '소속 대분류 그룹 ID (FK)',
    `category_code`     VARCHAR(10)  NOT NULL                 COMMENT '중분류 코드 (예: 101, 201)',
    `category_name`     VARCHAR(20)  NOT NULL                 COMMENT '중분류 명칭 (예: 급여, 생활비 통장)',
    `created_at`        DATETIME(6)  NOT NULL                 COMMENT '생성 일시',
    `updated_at`        DATETIME(6)  NOT NULL                 COMMENT '최종 수정 일시',
    CONSTRAINT `pk_category`           PRIMARY KEY (`category_id`),
    CONSTRAINT `uk_category_code`      UNIQUE (`category_code`),
    CONSTRAINT `fk_category_to_group`  FOREIGN KEY (`category_group_id`)
        REFERENCES `category_group` (`category_group_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='중분류 카테고리 테이블';
