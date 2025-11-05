package com.example.earthcafeserver.dto.product;

import com.example.earthcafeserver.domain.product.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductOptionResponse {
    private Long id;
    private String name;
    private Long extraPrice;

    public static ProductOptionResponse from(ProductOption option) {
        return ProductOptionResponse.builder()
                .id(option.getId())
                .name(option.getName())
                .extraPrice(option.getExtraPrice())
                .build();
    }
}
