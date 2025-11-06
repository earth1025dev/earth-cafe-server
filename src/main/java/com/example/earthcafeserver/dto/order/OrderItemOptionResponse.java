package com.example.earthcafeserver.dto.order;

import com.example.earthcafeserver.domain.order.OrderItemOption;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderItemOptionResponse {

    private Long optionId;
    private String optionName;
    private Long extraPrice;

    public static OrderItemOptionResponse from(OrderItemOption option) {
        return OrderItemOptionResponse.builder()
                .optionId(option.getId())
                .optionName(option.getProductOption().getName())
                .extraPrice(option.getExtraPrice())
                .build();
    }
}
