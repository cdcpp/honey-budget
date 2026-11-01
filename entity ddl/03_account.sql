-- ===================================================================
-- 3. account (계좌 마스터 테이블 DDL)
-- 엔티티: com.honeybudget.domain.Account
-- ===================================================================

CREATE TABLE IF NOT EXISTS `account` (
    `account_id`       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '계좌 고유 ID (PK)',
    `category_id`      BIGINT        NOT NULL                 COMMENT '계좌 목적 중분류 카테고리 ID (FK)',
    `account_name`     VARCHAR(50)   NOT NULL                 COMMENT '계좌 별칭/명칭 (예: 토스 생활비 통장)',
    `institution_name` VARCHAR(30)   NOT NULL                 COMMENT '금융기관/은행명 (예: 토스뱅크, 국민은행)',
    `use_yn`           TINYINT(1)    NOT NULL DEFAULT 1       COMMENT '계좌 활성 여부 (1: 사용중, 0: 해지/비활성 - Soft Delete)',
    `memo`             VARCHAR(200)  DEFAULT NULL             COMMENT '비고 및 메모',
    `created_at`       DATETIME(6)   NOT NULL                 COMMENT '생성 일시',
    `updated_at`       DATETIME(6)   NOT NULL                 COMMENT '최종 수정 일시',
    CONSTRAINT `pk_account`             PRIMARY KEY (`account_id`),
    CONSTRAINT `fk_account_to_category` FOREIGN KEY (`category_id`)
        REFERENCES `category` (`category_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='계좌 마스터 테이블';
