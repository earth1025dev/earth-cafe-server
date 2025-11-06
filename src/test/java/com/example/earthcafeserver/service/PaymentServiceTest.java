package com.example.earthcafeserver.service;

import com.example.earthcafeserver.domain.member.Gender;
import com.example.earthcafeserver.domain.member.Member;
import com.example.earthcafeserver.domain.member.Role;
import com.example.earthcafeserver.domain.order.Order;
import com.example.earthcafeserver.domain.order.OrderItem;
import com.example.earthcafeserver.domain.payment.Payment;
import com.example.earthcafeserver.domain.payment.PaymentStatus;
import com.example.earthcafeserver.domain.product.Product;
import com.example.earthcafeserver.domain.product.ProductOption;
import com.example.earthcafeserver.dto.payment.PaymentRequest;
import com.example.earthcafeserver.dto.payment.PaymentResponse;
import com.example.earthcafeserver.repository.MemberRepository;
import com.example.earthcafeserver.repository.OrderRepository;
import com.example.earthcafeserver.repository.PaymentRepository;
import com.example.earthcafeserver.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Member createMember() {
        return new Member("홍길동", "010-1234-5678", Gender.MALE, Role.BASIC_USER, LocalDate.of(1990, 1, 1));
    }

    private Product createProduct() {
        Product product = new Product("아메리카노", 4000L);
        product.addOption(new ProductOption(product, "HOT", 0L));
        product.addOption(new ProductOption(product, "ICE", 0L));
        product.addOption(new ProductOption(product, "샷추가 ", 500L));
        return product;
    }

    private Order createOrder(Member member, Product product, int quantity) {
        Order order = new Order(member);
        OrderItem orderItem = new OrderItem(order, product, quantity, product.getPrice());
        order.addItem(orderItem);
        return order;
    }

    @Test
    void requestPayment_success() {
        // given
        Member member = memberRepository.save(createMember());
        Product product = productRepository.save(createProduct());
        Order order = orderRepository.save(createOrder(member, product, 2));

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(order.getId());
        request.setAmount(order.getTotalAmount());
        request.setIdempotencyKey("pay-123");

        // then
        PaymentResponse response = paymentService.requestPayment(request);

        // when
        assertNotNull(response);
        assertEquals(order.getId(), response.getOrderId());
        assertTrue(response.isSuccess());
        assertEquals("SUCCESS", response.getStatus());
        assertNotNull(response.getPaymentId());

        Payment payment = paymentRepository.findByOrderId(response.getPaymentId()).orElseThrow();
        assertEquals(order.getId(), payment.getOrder().getId());
    }

    @Test
    void requestPayment_idempotent() {
        // given
        Member member = memberRepository.save(createMember());
        Product product = productRepository.save(createProduct());
        Order order = orderRepository.save(createOrder(member, product, 2));

        PaymentRequest request1 = new PaymentRequest();
        request1.setOrderId(order.getId());
        request1.setAmount(order.getTotalAmount());
        request1.setIdempotencyKey("pay-123");

        PaymentRequest request2 = new PaymentRequest();
        request2.setOrderId(order.getId());
        request2.setAmount(order.getTotalAmount());
        request2.setIdempotencyKey("pay-123");

        // then
        PaymentResponse response1 = paymentService.requestPayment(request1);
        PaymentResponse response2 = paymentService.requestPayment(request2);

        // when
        assertEquals(response1.getPaymentId(), response2.getPaymentId());
        assertTrue(response2.isIdempotentCheck());
    }

    @Test
    void requestPayment_orderNotFound() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(9999L);
        request.setAmount(1000L);

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.requestPayment(request));
    }

    @Test
    void requestPayment_fail_saved() {
        // given
        Member member = memberRepository.save(createMember());
        Product product = productRepository.save(createProduct());
        Order order = orderRepository.save(createOrder(member, product, 2));

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(order.getId());
        request.setAmount(order.getTotalAmount());
        request.setIdempotencyKey("fail-case");

        // when
        PaymentResponse response = paymentService.requestPayment(request);

        // then
        if (!response.isSuccess()) {
            assertNotNull(response.getFailCode());
            assertNotNull(response.getFailMessage());
        }
    }

    @Test
    void cancelPayment_success() {
        // given
        Member member = memberRepository.save(createMember());
        Product product = productRepository.save(createProduct());
        Order order = orderRepository.save(createOrder(member, product, 2));

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(order.getId());
        request.setAmount(order.getTotalAmount());
        request.setIdempotencyKey("pay-123");

        PaymentResponse response = paymentService.requestPayment(request);

        Payment payment = paymentRepository.findByOrderId(order.getId()).orElseThrow();
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        PaymentRequest request2 = new PaymentRequest();
        request2.setOrderId(order.getId());
        request2.setAmount(order.getTotalAmount());
        request2.setIdempotencyKey("pay-124");

        // then
        PaymentResponse response2 = paymentService.cancelPayment(request2);

        // when
        assertNotNull(response2);
        assertEquals("CANCELED", response2.getStatus());
    }

    @Test
    void cancelPayment_fail() {
        // given
        Member member = memberRepository.save(createMember());
        Product product = productRepository.save(createProduct());
        Order order = orderRepository.save(createOrder(member, product, 2));

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(order.getId());
        request.setAmount(order.getTotalAmount());
        request.setIdempotencyKey("pay-123");

        PaymentResponse response = paymentService.requestPayment(request);

        Payment payment = paymentRepository.findByOrderId(order.getId()).orElseThrow();
        payment.setPaymentStatus(PaymentStatus.FAIL);

        PaymentRequest request2 = new PaymentRequest();
        request2.setOrderId(order.getId());
        request2.setAmount(order.getTotalAmount());
        request2.setIdempotencyKey("pay-124");

        // when
        assertThrows(IllegalStateException.class, () -> paymentService.cancelPayment(request2));
    }
}