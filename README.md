# ☕🌍EarthCafeServer

카페 모바일 주문 백엔드 서버 과제  
Spring Boot + JPA + H2 기반의 REST API 구현

---
## 🧾 API 명세서 (EarthCafeServer)

> ☕ **모바일 카페 주문 백엔드 API**
>
> 회원 등록 → 상품 조회 → 주문 생성 → 결제 요청 → 주문 취소까지 전체 흐름을 다룹니다.  
> 모든 응답은 `application/json` 형식을 따릅니다.

---

### 👤 Member API

| Method | Endpoint | Description | Request Body                                                                                                     | Response |
|--------|-----------|--------------|------------------------------------------------------------------------------------------------------------------|-----------|
| `POST` | `/api/members` | 회원 등록 | `{ "name": "홍길동", "phone": "010-1234-5678", "gender": "MALE", "role": "BASIC_USER", "birthDate": "1995-02-15" }` | `201 Created` + 회원 정보 |
| `PUT` | `/api/members/{id}` | 회원 수정 | `{ "name": "김철수", "phone": "010-9999-8888" }`                                                                    | 수정된 회원 정보 |
| `GET` | `/api/members/{id}` | 회원 단건 조회 | -                                                                                                                | 회원 정보 |
| `POST` | `/api/members/{id}` | 회원 탈퇴 | -                                                                                                                | `204 No Content` |
| `POST` | `/api/members/{id}/cancel-withdrawal` | 회원 탈퇴 철회 | -                                                                                                                | `204 No Content` |

---

### ☕ Product API

| Method | Endpoint | Description | Request Body | Response |
|--------|-----------|--------------|---------------|-----------|
| `POST` | `/api/products` | 상품 등록 | `{ "name": "아메리카노", "price": 4000, "isActive": true, "options": [ {"name": "ICE", "extraPrice": 0}, {"name": "HOT", "extraPrice": 0} ] }` | `201 Created` + 상품 요약 정보 |
| `GET` | `/api/products` | 활성화된 상품 목록 조회 | - | `[ { "id": 1, "name": "아메리카노", "price": 4000 } ]` |
| `GET` | `/api/products/{productId}` | 상품 단건 조회 | - | 상품 상세 정보 |
| `PUT` | `/api/products/{productId}` | 상품 수정 | `{ "name": "카페라떼", "price": 4800 }` | 수정된 상품 요약 정보 |
| `PATCH` | `/api/products/{productId}/deactivate` | 상품 판매 중단 | - | `204 No Content` |
| `PATCH` | `/api/products/{productId}/activate` | 상품 재판매 시작 | - | `204 No Content` |

---


### 📦 Order / Payment API

| Method | Endpoint | Description | Request Body | Response |
|--------|-----------|--------------|---------------|-----------|
| `POST` | `/api/orders` | 주문 생성 | `{ "memberId": 1, "orders": [ { "productId": 1, "quantity": 2, "options": [1, 3] } ] }` | 주문 요약 정보 |
| `GET` | `/api/orders/{orderId}` | 주문 단건 조회 | - | 주문 상세 정보 (주문 항목, 상태 포함) |
| `GET` | `/api/orders?memberId={memberId}` | 회원별 주문 목록 조회 | - | 주문 목록 |
| `PATCH` | `/api/orders/{orderId}/cancel` | 주문 취소 | - | `204 No Content` |
| `GET` | `/api/orders/{orderId}/history` | 주문 이력 조회 | - | 주문 상태 변경 이력 |
| `GET` | `/api/orders/{orderId}/items` | 주문 항목 조회 | - | 주문에 포함된 상품 목록 |
| `POST` | `/api/orders/{orderId}/payments` | 결제 요청 | `{ "idempotencyKey": "uuid-12345" }` | `{ "paymentId": 1, "status": "SUCCESS", "elapsedMs": 540 }` |
| `PATCH` | `/api/orders/{orderId}/payments/cancel` | 결제 취소 | `{ "idempotencyKey": "uuid-12345" }` | `{ "paymentId": 1, "status": "SUCCESS", "elapsedMs": 540 } |
| `GET` | `/api/orders/{orderId}/payments` | 주문별 결제 내역 조회 | - | 결제 내역 목록 |
| `GET` | `/api/orders/{orderId}/payments/{paymentId}` | 결제 단건 조회 | - | 결제 상세 정보 (상태, 실패 사유 포함) |

---

### 🔁 상태 코드 요약

| 코드 | 의미 |
|------|------|
| `200 OK` | 요청 성공 |
| `201 Created` | 리소스 생성 완료 |
| `204 No Content` | 성공했으나 반환 데이터 없음 |
| `400 Bad Request` | 잘못된 요청 |
| `404 Not Found` | 대상 리소스 없음 |
| `500 Internal Server Error` | 서버 내부 오류 |

---

### 📚 예시 흐름

1. **회원 등록**  
   → `POST /api/members`
2. **상품 등록**  
   → `POST /api/products`
3. **주문 생성**  
   → `POST /api/orders`
4. **결제 요청**  
   → `POST /api/orders/{orderId}/payment`
5. **상품 판매 중단 / 재판매**  
   → `PATCH /api/products/{productId}/deactivate`  
   → `PATCH /api/products/{productId}/activate`
6. **주문 취소 (테스트용)**  
   → `DELETE /api/orders/{orderId}/cancel`

---

> ✅ **참고**
> - Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
> - API 문서는 OpenAPI 3.0 기준으로 자동 생성됩니다.

## 📊 ERD (Entity Relationship Diagram)

```mermaid
erDiagram
  MEMBER ||--o{ ORDER : places
  ORDER ||--o{ ORDER_ITEM : contains
  ORDER_ITEM ||--o{ ORDER_ITEM_OPTION : has
  ORDER_ITEM_OPTION }o--|| PRODUCT_OPTION : refers
  ORDER ||--|| PAYMENT : has
  PRODUCT ||--o{ PRODUCT_OPTION : has
  ORDER ||--o{ ORDER_HISTORY : logs

  MEMBER {
    bigint id PK
    varchar name
    varchar phone "UNIQUE"
    varchar gender
    date birth_date
    varchar status "ACTIVE|WITHDRAWN"
    varchar role "ADMIN|BASIC_USER|VIP_USER"
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

  ORDER_ITEM_OPTION {
    bigint id PK
    bigint order_item_id FK "-> ORDER_ITEM.id"
    bigint product_option_id FK "-> PRODUCT_OPTION.id"
    bigint extra_price
  }

  PAYMENT {
    bigint id PK
    bigint order_id FK "UNIQUE -> ORDER.id"
    varchar status "REQUESTED|SUCCESS|FAIL|REFUND_REQUESTED|REFUNDED|REFUND_FAIL"
    bigint amount
    varchar idempotency_key "UNIQUE"
    varchar fail_code
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