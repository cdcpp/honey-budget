-- ===================================================================
-- 1. category_group (대분류 그룹 테이블 DDL)
-- 엔티티: com.honeybudget.domain.CategoryGroup
-- ===================================================================

CREATE TABLE IF NOT EXISTS `category_group` (
    `category_group_id`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '대분류 그룹 고유 ID (PK)',
    `category_group_code` VARCHAR(10)  NOT NULL                 COMMENT '대분류 코드 (예: 100, 200, 300)',
    `category_group_name` VARCHAR(20)  NOT NULL                 COMMENT '대분류 명칭 (예: 수입, 저축 및 투자, 지출)',
    `created_at`          DATETIME(6)  NOT NULL                 COMMENT '생성 일시',
    `updated_at`          DATETIME(6)  NOT NULL                 COMMENT '최종 수정 일시',
    CONSTRAINT `pk_category_group` PRIMARY KEY (`category_group_id`),
    CONSTRAINT `uk_group_code`     UNIQUE (`category_group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='대분류 그룹 테이블';
