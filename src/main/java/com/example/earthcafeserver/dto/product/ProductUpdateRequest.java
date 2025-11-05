package com.example.earthcafeserver.dto.product;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductUpdateRequest {

    private String name;

    private Long price;
}
