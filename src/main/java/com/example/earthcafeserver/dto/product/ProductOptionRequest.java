package com.example.earthcafeserver.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductOptionRequest {

    @NotBlank
    private String name;

    @NotBlank
    private Long extraPrice;
}
