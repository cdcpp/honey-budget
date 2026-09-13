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
    erDiagram
    "계좌/자산(Asset)" ||--o{ "월간 현황(AssetSnapshot)" : "매월 1회 잔액 기록 (현황판)"
    "계좌/자산(Asset)" ||--o{ "지출 내역(Transaction)" : "돈이 빠져나가는 출처"
    "카테고리(Category)" ||--o{ "지출 내역(Transaction)" : "지출 성격"

    "계좌/자산(Asset)" {
        int 자산ID PK
        string 통장명 "예: 생활비통장, 고정비통장, 자동차통장, 적금, 주식"
        string 역할 "예: 지출용, 저축용, 투자용"
    }
    
    "월간 현황(AssetSnapshot)" {
        int 현황ID PK
        int 자산ID FK
        YearMonth 기준월 "예: 2026-09"
        decimal 현재잔액 "해당 월의 현황판 업데이트 금액"
    }

    "월간 예산/수입(MonthlyBudget)" {
        int 예산ID PK
        YearMonth 기준월 "예: 2026-09"
        decimal 총수입 "해당 월 급여"
        decimal 생활비할당 "910,000"
        decimal 고정비할당 "1,100,000"
    }

    "카테고리(Category)" {
        int 카테고리ID PK
        string 지출분류 "식비, 비고정지출, 고정비용, 자동차/병원비"
    }

    "지출 내역(Transaction)" {
        int 내역ID PK
        date 결제일자
        int 출금자산ID FK "어느 통장에서 나갔는가? (예: 생활비통장)"
        int 카테고리ID FK "어디에 썼는가? (예: 식비)"
        string 사용처 "예: 쿠팡, 주유소"
        decimal 결제금액
    }
```

