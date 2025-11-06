package com.example.earthcafeserver.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequest {

    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;

    @NotEmpty(message = "주문 상품은 한 개 이상이어야 합니다.")
    private List<@Valid OrderItemRequest> orderItems;

}
