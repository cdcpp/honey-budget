-- ===================================================================
-- 5. account_transaction (계좌별 일자 거래(지출) 내역 테이블 DDL)
-- 엔티티: com.honeybudget.domain.AccountTransaction
-- ===================================================================

CREATE TABLE IF NOT EXISTS `account_transaction` (
    `account_transaction_id` BIGINT         NOT NULL AUTO_INCREMENT COMMENT '거래 내역 고유 ID (PK)',
    `account_id`             BIGINT         NOT NULL                 COMMENT '대상 계좌 ID (FK)',
    `year_month`             VARCHAR(7)     NOT NULL                 COMMENT '월별 시트 기준 연월 (YYYY-MM 형식: 예 2025-01)',
    `transaction_date`       DATE           NOT NULL                 COMMENT '거래 일자 (YYYY-MM-DD 형식: 예 2025-01-15)',
    `amount`                 DECIMAL(15, 0) NOT NULL                 COMMENT '지출/거래 금액',
    `description`            VARCHAR(100)   NOT NULL                 COMMENT '사용처 / 내용 (예: 이마트 장보기, 주유비)',
    `memo`                   VARCHAR(255)   DEFAULT NULL             COMMENT '비고 및 메모',
    `created_at`             DATETIME(6)    NOT NULL                 COMMENT '생성 일시',
    `updated_at`             DATETIME(6)    NOT NULL                 COMMENT '최종 수정 일시',
    CONSTRAINT `pk_account_transaction` PRIMARY KEY (`account_transaction_id`),
    CONSTRAINT `fk_account_transaction_to_account` FOREIGN KEY (`account_id`)
        REFERENCES `account` (`account_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX `idx_account_trans_ym`   (`account_id`, `year_month`),
    INDEX `idx_account_trans_date` (`account_id`, `transaction_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='계좌별 일자 거래(지출) 내역 테이블';
