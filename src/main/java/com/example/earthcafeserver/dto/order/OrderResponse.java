package com.example.earthcafeserver.dto.order;

import com.example.earthcafeserver.domain.order.Order;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class OrderResponse {

    private Long orderId;

    private Long memberId;

    private String orderStatus;

    private Long totalAmount;

    private LocalDateTime orderAt;

    private List<OrderItemResponse> orderItems;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .memberId(order.getMember().getId())
                .orderStatus(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .orderAt(order.getCreateAt())
                .orderItems(order.getItems() == null ? null :
                        order.getItems().stream()
                                .map(OrderItemResponse::from)
                                .toList())
                .build();
    }
}
