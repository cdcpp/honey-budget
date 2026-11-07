# 🍯 HoneyBudget (허니버짓)
> **Where Love and Hope Sprout** 🌱  
>  혼자쓰려고 만드는 희망이 싹트는 달콤한 우리 집 가계부

## 📌 Features
- [ ] 카테고리(Category)별 지출/수입 분류
- [ ] 거래 내역(Transaction) CRUD
- [ ] 월별 지출 합계 및 예산 관리

## 🛠 Tech Stack
- **Language:** Java 17
- **Framework:** Spring Boot 4.1
- **ORM:** : Spring Data JPA
- **Database:** H2 (Dev), MySQL


## 🧩 Database Modeling
```mermaid
erDiagram i
    CATEGORY_GROUP ||--o{ CATEGORY : "대분류-중분류"
    CATEGORY ||--o{ ACCOUNT : "계좌 목적 분류"
    ACCOUNT ||--o{ ACCOUNTYEARMONTH : "연도별 월 금액 (계좌+연도당 1행)"
    ACCOUNT ||--o{ ACCOUNT_TRANSACTION : "일자별 거래 내역"

    CATEGORY_GROUP {
        bigint category_group_id PK
        varchar category_group_code UK "대분류 코드 (예: 100, 200)"
        varchar category_group_name "대분류 명칭"
        datetime created_at
        datetime updated_at
    }

    CATEGORY {
        bigint category_id PK
        bigint category_group_id FK "대분류 그룹"
        varchar category_code UK "중분류 코드 (예: 101, 201)"
        varchar category_name "중분류 명칭 (예: 생활비, 비상금)"
        datetime created_at
        datetime updated_at
    }

    ACCOUNT {
        bigint account_id PK
        varchar account_name "계좌 별칭 (예: 토스 생활비 통장)"
        varchar institution_name "금융기관명 (예: 토스뱅크)"
        bigint category_id FK "계좌 목적 카테고리 (중분류)"
        boolean use_yn "활성 여부 (Soft Delete)"
        varchar memo "비고/메모"
        datetime created_at
        datetime updated_at
    }

    ACCOUNTYEARMONTH {
        bigint account_yearmonth_id PK
        bigint account_id FK "계좌 (UK: account_id + base_year)"
        int base_year "연도 (예: 2026)"
        decimal month_01 "1월 금액"
        decimal month_02 "2월 금액"
        decimal month_03 "3월 금액"
        decimal month_04 "4월 금액"
        decimal month_05 "5월 금액"
        decimal month_06 "6월 금액"
        decimal month_07 "7월 금액"
        decimal month_08 "8월 금액"
        decimal month_09 "9월 금액"
        decimal month_10 "10월 금액"
        decimal month_11 "11월 금액"
        decimal month_12 "12월 금액"
        datetime created_at
        datetime updated_at
    }

    ACCOUNT_TRANSACTION {
        bigint account_transaction_id PK
        bigint account_id FK "대상 계좌"
        varchar year_month "월별 시트 기준 연월 (YYYY-MM)"
        date transaction_date "거래 일자"
        decimal amount "거래 금액"
        varchar description "사용처/내용 (예: 이마트 장보기)"
        varchar memo "비고/메모"
        datetime created_at
        datetime updated_at
    }
```
