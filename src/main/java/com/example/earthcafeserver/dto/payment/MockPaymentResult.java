package com.example.earthcafeserver.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MockPaymentResult {

    private final boolean success;
    private final String failCode;
    private final String failMessage;
    private final long elapsedMillis;
}
