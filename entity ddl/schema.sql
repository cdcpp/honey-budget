-- ===================================================================
-- Honey Budget (가계부 시스템) - MySQL DDL Schema
-- 생성 대상: CategoryGroup, Category, Account, AccountYearMonth, AccountTransaction
-- 문자셋: utf8mb4 / 콜레이션: utf8mb4_unicode_ci
-- ===================================================================

-- 외래키 체크 일시 비활성화 (순서 무관 드롭/생성 지원)
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `account_transaction`;
DROP TABLE IF EXISTS `accountyearmonth`;
DROP TABLE IF EXISTS `account`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `category_group`;

SET FOREIGN_KEY_CHECKS = 1;

-- -------------------------------------------------------------------
-- 1. category_group (대분류 그룹: 수입, 지출, 저축 등)
-- -------------------------------------------------------------------
CREATE TABLE `category_group` (
    `category_group_id`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '대분류 그룹 고유 ID (PK)',
    `category_group_code` VARCHAR(10)  NOT NULL                 COMMENT '대분류 코드 (예: 100, 200, 300)',
    `category_group_name` VARCHAR(20)  NOT NULL                 COMMENT '대분류 명칭 (예: 수입, 저축 및 투자, 지출)',
    `created_at`          DATETIME(6)  NOT NULL                 COMMENT '생성 일시',
    `updated_at`          DATETIME(6)  NOT NULL                 COMMENT '최종 수정 일시',
    CONSTRAINT `pk_category_group` PRIMARY KEY (`category_group_id`),
    CONSTRAINT `uk_group_code`     UNIQUE (`category_group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='대분류 그룹 테이블';

-- -------------------------------------------------------------------
-- 2. category (중분류 카테고리: 급여, 생활비 통장, 비상금 등)
-- -------------------------------------------------------------------
CREATE TABLE `category` (
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

-- -------------------------------------------------------------------
-- 3. account (계좌 마스터: 통장/증권계좌)
-- -------------------------------------------------------------------
CREATE TABLE `account` (
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

-- -------------------------------------------------------------------
-- 4. accountyearmonth (계좌별 연간 1~12월 금액 요약 / 명세표)
-- -------------------------------------------------------------------
CREATE TABLE `accountyearmonth` (
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

-- -------------------------------------------------------------------
-- 5. account_transaction (계좌별 일자 가계부 / 실거래 지출 내역)
-- -------------------------------------------------------------------
CREATE TABLE `account_transaction` (
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
