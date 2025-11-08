package com.example.earthcafeserver.controller;

import com.example.earthcafeserver.dto.order.OrderRequest;
import com.example.earthcafeserver.dto.payment.PaymentRequest;
import com.example.earthcafeserver.service.OrderService;
import com.example.earthcafeserver.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    private final PaymentService paymentService;

    /**
     * 주문 생성
     *
     * @return
     */
    @Operation(summary = "주문 생성", description = "새로운 주문을 등록합니다.")
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.insertOrder(request));
    }

    /**
     * 주문 단건 조회
     *
     * @param orderId
     * @return
     */
    @Operation(summary = "주문 단건 조회", description = "주문 번호로 주문을 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    /**
     * 회원별 주문 목록 조회
     *
     * @param memberId
     * @return
     */
    @Operation(summary = "회원별 주문 목록 조회", description = "회원 별 주문 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getOrders(@PathVariable Long memberId) {
        return ResponseEntity.ok(orderService.getOrdersByMember(memberId));
    }

    /**
     * 주문 취소
     * @param orderId
     * @return
     */
    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        //TODO changeBy
        orderService.cancelOrder(orderId, "SYSTEM");
        return ResponseEntity.noContent().build();
    }

    /**
     * 주문 이력 조회
     * @param orderId
     * @return
     */
    @Operation(summary = "주문 이력 조회", description = "주문 번호 기준 이력을 조회합니다.")
    @GetMapping("/{orderId}/history")
    public ResponseEntity<?> getOrderHistory(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderHistory(orderId));
    }

    /**
     * 주문 항목 조회
     * @param orderId
     * @return
     */
    @Operation(summary = "주문 항목 조회", description = "주문 번호 기준 주문 항목을 조회합니다.")
    @GetMapping("/{orderId}/items")
    public ResponseEntity<?> getOrderItems(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }

    /**
     * 결제 요청
     * @param request
     * @return
     */
    @Operation(summary = "결제 요청", description = "결제를 요청합니다.")
    @PostMapping("/{orderId}/payments")
    public ResponseEntity<?> payment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.requestPayment(request));
    }

    /**
     * 결제 취소
     * @param request
     * @return
     */
    @Operation(summary = "결제 취소", description = "결제를 취소합니다.")
    @PatchMapping("/{orderId}/payments/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.cancelPayment(request));
    }

    /**
     * 결제 내역 조회
     * @param orderId
     * @return
     */
    @Operation(summary = "결제 내역 조회", description = "주문 번호 기준 결제 내역을 조회합니다.")
    @GetMapping("/{orderId}/payments")
    public ResponseEntity<?> getPaymentRequests(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    /**
     * 결제 단건 조회
     * @param orderId
     * @param paymentId
     * @return
     */
    @Operation(summary = "결제 단건 조회", description = "결제 번호 기준 결제 내역을 조회합니다.")
    @GetMapping("/{orderId}/payments/{paymentId}")
    public ResponseEntity<?> getPaymentRequest(@PathVariable Long orderId, @PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderIdAndPaymentId(orderId, paymentId));
    }
}
