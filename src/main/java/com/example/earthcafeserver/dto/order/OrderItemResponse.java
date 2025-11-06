package com.example.earthcafeserver.dto.order;

import com.example.earthcafeserver.domain.order.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderItemResponse {

    private Long productId;

    private String productName;

    private int quantity;

    private Long unitPrice;

    private Long lineAmount;

    private List<OrderItemOptionResponse> options;

    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .lineAmount(orderItem.getLineAmount())
                .options(orderItem.getOptions() == null ? null :
                        orderItem.getOptions().stream()
                                .map(OrderItemOptionResponse::from)
                                .toList())
                .build();
    }
}
