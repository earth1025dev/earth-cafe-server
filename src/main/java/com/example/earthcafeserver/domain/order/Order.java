package com.example.earthcafeserver.domain.order;

import com.example.earthcafeserver.domain.common.BaseEntity;
import com.example.earthcafeserver.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_orders_member"))
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount = 0L;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
    }

    public Order(Member member) {
        this.member = member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
        recalcTotal();
    }
    public void recalcTotal() {
        this.totalAmount = items.stream().mapToLong(OrderItem::getLineAmount).sum();
    }
}
