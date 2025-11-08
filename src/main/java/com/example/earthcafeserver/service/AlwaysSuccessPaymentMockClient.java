package com.example.earthcafeserver.service;

import com.example.earthcafeserver.dto.payment.MockPaymentResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class AlwaysSuccessPaymentMockClient extends PaymentMockClient {

    @Override
    public MockPaymentResult paymentResult() {
        return new MockPaymentResult(true, null, null, 100L);
    }
}
