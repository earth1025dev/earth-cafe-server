package com.example.earthcafeserver.dto.payment;

import com.example.earthcafeserver.domain.payment.Payment;
import com.example.earthcafeserver.domain.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private boolean success;

    private String status;

    private String failCode;

    private String failMessage;

    private Long paymentId;

    private Long orderId;

    private boolean idempotentCheck;

    private long elapsedMillis;

    public static PaymentResponse from(Payment payment, boolean idempotentCheck) {
        return PaymentResponse.builder()
                .success(payment.getPaymentStatus() == PaymentStatus.SUCCESS)
                .status(payment.getPaymentStatus().name())
                .failCode(payment.getFailCode())
                .failMessage(payment.getFailReason())
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .idempotentCheck(idempotentCheck)
                .build();
    }
}
