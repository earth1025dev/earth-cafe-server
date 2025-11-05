package com.example.earthcafeserver.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOptionRequest {

    @NotBlank
    private String name;

    @NotBlank
    private Long extraPrice;
}
