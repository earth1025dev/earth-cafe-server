package com.example.earthcafeserver.service;

import com.example.earthcafeserver.domain.order.Order;
import com.example.earthcafeserver.domain.order.OrderHistory;
import com.example.earthcafeserver.domain.order.OrderStatus;
import com.example.earthcafeserver.domain.payment.Payment;
import com.example.earthcafeserver.domain.payment.PaymentStatus;
import com.example.earthcafeserver.dto.payment.MockPaymentResult;
import com.example.earthcafeserver.dto.payment.PaymentRequest;
import com.example.earthcafeserver.dto.payment.PaymentResponse;
import com.example.earthcafeserver.repository.OrderHistoryRepository;
import com.example.earthcafeserver.repository.OrderRepository;
import com.example.earthcafeserver.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    private final OrderHistoryRepository orderHistoryRepository;

    private final PaymentMockClient paymentMockClient;

    public PaymentResponse requestPayment(PaymentRequest request) {

        // 1) 멱등성 키로 먼저 조회, 같은 키로 처리 여부 확인
        if (request.getIdempotencyKey() != null) {
            Optional<Payment> existed = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());

            // 같은 키로 처리했을 시 해당 결제 내용으로 전달
            if (existed.isPresent()) {
                return PaymentResponse.from(existed.get(), true);
            }
        }

        // 2) 주문 조회
        Long orderId = request.getOrderId();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

        // 3) 모킹 결제 호출
        MockPaymentResult result = paymentMockClient.paymentResult();

        // 4) 결과 Payment 엔티티에 기록
        Payment payment = new Payment(order, order.getTotalAmount(), request.getIdempotencyKey());
        payment.setRequestedAt(LocalDateTime.now());

        if (result.isSuccess()) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());

            orderHistoryRepository.save(
                    new OrderHistory(order, order.getStatus(), OrderStatus.CONFIRMED, "SYSTEM")
            );
            order.changeStatus(OrderStatus.CONFIRMED);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAIL);
            payment.setFailCode(result.getFailCode());
            payment.setFailReason(result.getFailMessage());

            orderHistoryRepository.save(
                    new OrderHistory(order, order.getStatus(), OrderStatus.FAILED_PAYMENT, "SYSTEM")
            );
            order.changeStatus(OrderStatus.FAILED_PAYMENT);
        }
        paymentRepository.save(payment);

        // 5) 응답 전달
        return PaymentResponse.from(payment, false);
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
}
