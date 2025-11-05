package com.example.earthcafeserver.dto.payment;

import lombok.Getter;

@Getter
public class PaymentRequest {

    private Long orderId;

    private Long amount;

    private String idempotencyKey;
}
