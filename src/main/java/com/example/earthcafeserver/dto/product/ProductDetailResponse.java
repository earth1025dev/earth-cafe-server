package com.example.earthcafeserver.dto.product;

import com.example.earthcafeserver.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponse {
    private Long id;

    private String name;

    private Long price;

    private Boolean isActive;

    private List<ProductOptionResponse> options;

    public static ProductDetailResponse from(Product product) {
        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .options(product.getOptions() == null ? null :
                        product.getOptions().stream()
                                .map(ProductOptionResponse::from)
                                .toList())
                .build();
    }
}
