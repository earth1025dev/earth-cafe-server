package com.example.earthcafeserver.domain.order;

import com.example.earthcafeserver.domain.product.ProductOption;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "order_item_option")
public class OrderItemOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;

    @Column(name = "extra_price", nullable = false)
    private Long extraPrice;

    protected OrderItemOption() {}

    public OrderItemOption(ProductOption productOption, Long extraPrice) {
        this.productOption = productOption;
        this.extraPrice = extraPrice;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
