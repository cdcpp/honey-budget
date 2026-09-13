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
2026년 9월 13일 20시 45분 33초 기준으로 깃허브(GitHub)가 지원하는 내장 Mermaid 렌더링 엔진의 엄격한 문법 스펙을 확인해 본 결과, 엔티티(테이블) 이름에 한글, 특수문자(/, ()), 띄어쓰기가 포함되어 파싱 에러가 발생한 것입니다.

깃허브 환경에서 에러 없이 깔끔하게 렌더링되도록, 엔티티명은 영문 대문자로 변경하고 한글 설명은 속성 옆에 주석 형태로 달아두는 안전한 표준 문법으로 수정했습니다.

아래 코드를 그대로 복사해서 깃허브 README.md에 붙여넣으시면 정상적으로 그려집니다.

Markdown
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
        decimal living_expense "생활비 할당 (91만원)"
        decimal fixed_expense "고정비 할당 (110만원)"
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
```
