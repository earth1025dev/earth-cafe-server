package com.example.earthcafeserver.domain.order;

import com.example.earthcafeserver.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "order_history")
public class OrderHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "fk_order_history_order"))
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "prev_status", length = 30)
    private OrderStatus prevStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 30)
    private OrderStatus newStatus;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    @Column(name = "changed_by", length = 50)
    private String changedBy;

    protected OrderHistory() {}
    public OrderHistory(Order order, OrderStatus prevStatus, OrderStatus newStatus, String changedBy) {
        this.order = order; this.prevStatus = prevStatus; this.newStatus = newStatus; this.changedBy = changedBy;
    }
}
