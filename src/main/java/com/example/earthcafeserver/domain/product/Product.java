package com.example.earthcafeserver.domain.product;

import com.example.earthcafeserver.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "product")
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(name ="is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    protected Product() {}

    public Product(String name, Long price) {
        this.name = name;
        this.price = price;
        this.isActive = true;
    }

    public void addOption(ProductOption option) {
        options.add(option);
        option.setProduct(this);
    }
}
