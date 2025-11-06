package com.example.earthcafeserver.dto.order;

import com.example.earthcafeserver.domain.order.Order;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class OrderSummaryResponse {

    private Long orderId;

    private Long memberId;

    private String orderStatus;

    private Long totalAmount;

    private LocalDateTime orderAt;

    public static OrderSummaryResponse from(Order order) {
        return OrderSummaryResponse.builder()
                .orderId(order.getId())
                .memberId(order.getMember().getId())
                .orderStatus(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .orderAt(order.getCreateAt())
                .build();
    }
}
