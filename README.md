# ☕🌍EarthCafeServer

카페 모바일 주문 백엔드 서버 과제  
Spring Boot + JPA + H2 기반의 REST API 구현

---

## 📊 ERD (Entity Relationship Diagram)

```mermaid
erDiagram
  MEMBER ||--o{ ORDER : places
  ORDER ||--o{ ORDER_ITEM : contains
  ORDER ||--|| PAYMENT : has
  PRODUCT ||--o{ ORDER_ITEM : appears_in
  PRODUCT ||--o{ PRODUCT_OPTION : has
  ORDER ||--o{ ORDER_HISTORY : logs

  MEMBER {
    bigint id PK
    varchar name
    varchar phone "UNIQUE"
    varchar gender
    date birth_date
    varchar status "ACTIVE|WITHDRAWN"
    timestamp withdrawn_at
    timestamp created_on
    timestamp updated_on
  }

  PRODUCT {
    bigint id PK
    varchar name
    bigint price
    boolean is_active
    timestamp created_on
    timestamp updated_on
  }

  PRODUCT_OPTION {
    bigint id PK
    bigint product_id FK "-> PRODUCT.id"
    varchar name
    bigint extra_price
    timestamp created_on
    timestamp updated_on
  }

  ORDER {
    bigint id PK
    bigint member_id FK "-> MEMBER.id"
    varchar status "PENDING_PAYMENT|CONFIRMED|CANCELED|FAILED_PAYMENT|CANCEL_FAILED"
    bigint total_amount
    timestamp created_on
    timestamp updated_on
  }

  ORDER_ITEM {
    bigint id PK
    bigint order_id FK "-> ORDER.id"
    bigint product_id FK "-> PRODUCT.id"
    int quantity
    bigint unit_price
    bigint line_amount
  }

  PAYMENT {
    bigint id PK
    bigint order_id FK "UNIQUE -> ORDER.id"
    varchar status "REQUESTED|SUCCESS|FAIL|REFUND_REQUESTED|REFUNDED|REFUND_FAIL"
    bigint amount
    varchar idempotency_key "UNIQUE"
    varchar approval_number
    varchar refund_number
    varchar fail_reason
    timestamp requested_at
    timestamp completed_at
    timestamp updated_on
  }

  ORDER_HISTORY {
    bigint id PK
    bigint order_id FK "-> ORDER.id"
    varchar prev_status
    varchar new_status
    timestamp changed_at
    varchar changed_by
  }
