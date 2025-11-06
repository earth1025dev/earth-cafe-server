package com.example.earthcafeserver.service;

import com.example.earthcafeserver.dto.payment.MockPaymentResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class PaymentMockClient {

    private static final List<String> FAIL_CODES = List.of(
            "GATEWAY_TIMEOUT",
            "NETWORK_ERROR",
            "PAYMENT_DECLINED",
            "DUPLICATE_REQUEST"
    );

    private static final Map<String, String> FAIL_MESSAGES = Map.of(
            "GATEWAY_TIMEOUT", "결제 응답이 지연되었습니다.",
            "NETWORK_ERROR", "결제 서버와 통신에 실패했습니다.",
            "PAYMENT_DECLINED", "카드사에서 결제를 거절했습니다.",
            "DUPLICATE_REQUEST", "중복된 결제 요청입니다."
    );

    private final Random random = new Random();

    public MockPaymentResult paymentResult() {
        long start = System.currentTimeMillis();

        simulateDelay();

        boolean failed = random.nextInt(100) < 20;
        long elapsed = System.currentTimeMillis() - start;

        if (failed) {
            String failCode = pickFailCode();
            return new MockPaymentResult(false, failCode, FAIL_MESSAGES.getOrDefault(failCode, "결제 처리에 실패했습니다."), elapsed);
        }

        return new MockPaymentResult(true, null, null, elapsed);
    }

    private void simulateDelay() {
        try {
            Thread.sleep(200 + random.nextInt(1300)); // 0.1~5초 사이 랜덤 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String pickFailCode() {
        return FAIL_CODES.get(random.nextInt(FAIL_CODES.size()));
    }
}
