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
    ASSETS ||--o{ TRANSACTIONS : "uses"
    CATEGORIES ||--o{ TRANSACTIONS : "classifies"
    TRANSACTIONS ||--o{ TRANSACTION_DETAILS : "contains"

    ASSETS {
        int asset_id PK
        string asset_name
        string asset_type
        decimal balance
        string memo
    }
    CATEGORIES {
        int category_id PK
        string type
        string main_category
        string sub_category
    }
    TRANSACTIONS {
        int transaction_id PK
        date transaction_date
        int asset_id FK
        int category_id FK
        string merchant
        decimal total_amount
        string memo
    }
    TRANSACTION_DETAILS {
        int detail_id PK
        int transaction_id FK
        string item_name
        decimal item_price
    }
```
