package com.example.earthcafeserver.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProductRequest {

    @NotBlank
    private String name;

    @NotBlank
    private Long price;

    @NotBlank
    private Boolean isActive;

    private List<ProductOptionRequest> options;
}
