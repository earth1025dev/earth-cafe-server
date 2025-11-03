package com.example.earthcafeserver.domain.order;

public enum OrderStatus {
    PENDING_PAYMENT,
    CONFIRMED,
    CANCELED,
    FAILED_PAYMENT,
    CANCEL_FAILED
}
