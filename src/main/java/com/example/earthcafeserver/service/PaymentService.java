package com.example.earthcafeserver.service;

import com.example.earthcafeserver.domain.order.Order;
import com.example.earthcafeserver.domain.payment.Payment;
import com.example.earthcafeserver.domain.payment.PaymentStatus;
import com.example.earthcafeserver.dto.payment.PaymentRequest;
import com.example.earthcafeserver.dto.payment.PaymentResponse;
import com.example.earthcafeserver.repository.OrderRepository;
import com.example.earthcafeserver.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    public PaymentResponse requestPayment(PaymentRequest request) {
        Long orderId = request.getOrderId();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

        String result;
        try {
            result = makePayment();
        } catch (Exception e) {
            Payment fail = paymentRepository.save(new Payment(order, request.getAmount(), PaymentStatus.FAIL, ""));
            fail.setFailCode("NETWORK_ERROR");
            fail.setFailCode("네트워크 오류로 실패");

            return PaymentResponse.from(fail, true);
        }

        Payment success = paymentRepository.save(new Payment(order, request.getAmount(), PaymentStatus.SUCCESS, ""));
        return PaymentResponse.from(success, true);
    }

    public PaymentResponse getPaymentByOrderId(PaymentRequest request) {
        Payment payment = paymentRepository.findByOrderId(request.getOrderId()).orElseThrow(() -> new IllegalArgumentException("결제 내역이 없습니다."));

        return PaymentResponse.from(payment, true);
    }

    public PaymentResponse cancelPayment(PaymentRequest request) {
        Payment payment = paymentRepository.findByOrderId(request.getOrderId()).orElseThrow(() -> new IllegalArgumentException("결제 내역이 없습니다."));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("결제 취소는 성공된 결제만 가능합니다.");
        }

        payment.setPaymentStatus(PaymentStatus.CANCELED);

        return PaymentResponse.from(payment, true);
    }

    private String makePayment() throws Exception {
        Thread.sleep((long) (Math.random() * 1000));

        Random random = new Random();
        if (random.nextInt() % 100 == 1) {
            throw new Exception("Failed!");
        }

        return "Success";
    }
}
