package com.example.earthcafeserver.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentRequest {

    private Long orderId;

    private Long amount;

    private String idempotencyKey;
}
