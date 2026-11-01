-- ===================================================================
-- 4. accountyearmonth (계좌별 연간 1~12월 금액 관리 테이블 DDL)
-- 엔티티: com.honeybudget.domain.AccountYearMonth
-- ===================================================================

CREATE TABLE IF NOT EXISTS `accountyearmonth` (
    `account_yearmonth_id` BIGINT         NOT NULL AUTO_INCREMENT COMMENT '계좌 연월 고유 ID (PK)',
    `account_id`           BIGINT         NOT NULL                 COMMENT '대상 계좌 ID (FK)',
    `year`                 INT            NOT NULL                 COMMENT '연도 (예: 2025, 2026)',
    `month_01`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '1월 금액',
    `month_02`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '2월 금액',
    `month_03`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '3월 금액',
    `month_04`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '4월 금액',
    `month_05`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '5월 금액',
    `month_06`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '6월 금액',
    `month_07`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '7월 금액',
    `month_08`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '8월 금액',
    `month_09`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '9월 금액',
    `month_10`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '10월 금액',
    `month_11`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '11월 금액',
    `month_12`             DECIMAL(15, 0) NOT NULL DEFAULT 0       COMMENT '12월 금액',
    `created_at`           DATETIME(6)    NOT NULL                 COMMENT '생성 일시',
    `updated_at`           DATETIME(6)    NOT NULL                 COMMENT '최종 수정 일시',
    CONSTRAINT `pk_accountyearmonth` PRIMARY KEY (`account_yearmonth_id`),
    CONSTRAINT `uk_account_year`     UNIQUE (`account_id`, `year`),
    CONSTRAINT `fk_account_yearmonth_to_account` FOREIGN KEY (`account_id`)
        REFERENCES `account` (`account_id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='계좌별 연간 1~12월 금액 관리 테이블';
