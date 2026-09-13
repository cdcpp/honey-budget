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

```mermaid
erDiagram
    "자산" ||--o{ "거래내역" : "결제수단으로 사용됨"
    "카테고리" ||--o{ "거래내역" : "수입/지출로 분류됨"
    "거래내역" ||--o{ "거래상세" : "개별 품목을 포함함"

    "자산" {
        int 자산ID PK
        string 자산명 "예: 비상금1, 주식"
        string 자산유형 "예: 현금, 예적금"
        decimal 잔액
        string 메모
    }
    "카테고리" {
        int 카테고리ID PK
        string 수지구분 "수입/지출/이체"
        string 대분류 "예: 식비, 고정지출"
        string 소분류 "예: 마트, 보험료"
    }
    "거래내역" {
        int 거래ID PK
        date 거래일자
        int 자산ID FK "돈이 나간/들어온 곳"
        int 카테고리ID FK "지출/수입 성격"
        string 사용처 "예: 쿠팡, 주유소"
        decimal 총금액 "영수증 총액"
        string 메모
    }
    "거래상세" {
        int 상세ID PK
        int 거래ID FK
        string 품목명 "예: 앞다리살, 블루베리"
        decimal 품목금액 "개별 가격"
    }
```
