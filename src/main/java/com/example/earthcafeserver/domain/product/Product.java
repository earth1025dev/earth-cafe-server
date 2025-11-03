package com.example.earthcafeserver.domain.product;

import com.example.earthcafeserver.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "product")
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(name ="is_active", nullable = false)
    private Boolean isActive;

    protected Product() {}

    public Product(String name, Long price) {
        this.name = name;
        this.price = price;
    }
}
