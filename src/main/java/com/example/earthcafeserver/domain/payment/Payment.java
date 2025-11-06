package com.example.earthcafeserver.domain.payment;

import com.example.earthcafeserver.domain.common.BaseEntity;
import com.example.earthcafeserver.domain.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_idempotency", columnNames = "idempotency_key"),
        @UniqueConstraint(name = "uk_payment_order", columnNames = "order_id")
})
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", unique = true, foreignKey = @ForeignKey(name = "fk_payment_order"))
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.REQUESTED;

    @Column(nullable = false)
    private Long amount;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    @Column(name = "fail_code")
    private String failCode;

    @Column(name = "fail_reason", length = 255)
    private String failReason;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected Payment() {
    }

    public Payment(Order order, Long amount, String idempotencyKey) {
        this.order = order;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
    }
}
