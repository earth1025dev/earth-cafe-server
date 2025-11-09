package com.example.earthcafeserver.dto.order;

import com.example.earthcafeserver.domain.order.OrderHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderHistoryResponse {

    private Long id;
    private Long orderId;
    private String prevStatus;
    private String newStatus;
    private LocalDateTime changeAt;

    public static OrderHistoryResponse from(OrderHistory orderHistory) {
        return OrderHistoryResponse.builder()
                .id(orderHistory.getId())
                .orderId(orderHistory.getOrder().getId())
                .prevStatus(orderHistory.getPrevStatus() == null ? null :
                        orderHistory.getPrevStatus().name())
                .newStatus(orderHistory.getNewStatus().name())
                .changeAt(orderHistory.getChangedAt())
                .build();
    }
}
