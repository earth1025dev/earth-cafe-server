package com.example.earthcafeserver.domain.product;

import com.example.earthcafeserver.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "product_option")
public class ProductOption extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_product_option_product"))
    private Product product;

    @Column(nullable = false)
    private String name;

    @Column(name = "extra_price", nullable = false)
    private Long extraPrice;

    protected ProductOption() {}

    public ProductOption(Product product, String name, Long extraPrice) {
        this.product = product;
        this.name = name;
        this.extraPrice = extraPrice;
    }
}
