# 🍯 HoneyBudget (허니버짓)
> **Where Love and Hope Sprout** 🌱  
>  혼자쓰려고 만드는 희망이 싹트는 달콤한 우리 집 가계부 

## 📌 Features
- [ ] 카테고리(Category)별 지출/수입 분류
- [ ] 거래 내역(Transaction) CRUD
- [ ] 월별 지출 합계 및 예산 관리

## 🛠 Tech Stack
- **Language:** Java 17 
- **Framework:** Spring Boot 3.x
- **ORM:** : JPA
- **Database:** H2 (Dev), MySQL


## 🧩 Database Modeling 
```mermaid
erDiagram
    ASSET ||--o{ ASSET_SNAPSHOT : "매월 1회 잔액 기록"
    ASSET ||--o{ TRANSACTION : "출금 통장"
    CATEGORY ||--o{ TRANSACTION : "지출 분류"

    ASSET {
        int asset_id PK
        string name "통장명 (생활비, 고정비, 자동차, 주식 등)"
        string role "역할"
    }
    
    ASSET_SNAPSHOT {
        int snapshot_id PK
        int asset_id FK
        string record_month "기준월 (예: 2026-09)"
        decimal balance "현황판 업데이트 시점의 현재 잔액"
    }

    MONTHLY_BUDGET {
        int budget_id PK
        string record_month "기준월"
        decimal total_income "월 총수입"
        decimal living_expense "생활비 할당"
        decimal fixed_expense "고정비 할당"
    }

    CATEGORY {
        int category_id PK
        string type "분류명 (식비, 고정비용, 비고정지출, 자동차/병원비)"
    }

    TRANSACTION {
        int transaction_id PK
        date pay_date "결제일자"
        int asset_id FK "돈이 나간 통장 (출금자산ID)"
        int category_id FK "지출 목적 (카테고리ID)"
        string merchant "사용처 (예: 쿠팡, 주유소)"
        decimal amount "결제금액"
    }

    TRANSACTION_DETAIL {
        int detail_id PK
        int transaction_id FK
        string item_name "품목명 (예: 닭안심, 블루베리)"
        decimal amount "개별 금액"
    }
```
