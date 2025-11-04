package com.example.earthcafeserver.repository;

import com.example.earthcafeserver.domain.payment.Payment;
import com.example.earthcafeserver.domain.payment.PaymentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PaymentRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Payment> findByOrderId(Long orderId) {
        return em.createQuery("select p from Payment p where p.order.id = :orderId", Payment.class)
                .setParameter("orderId", orderId)
                .getResultStream().findFirst();
    }

    public Optional<Payment> findByIdempotencyKey(String key) {
        return em.createQuery("select p from Payment p where p.idempotencyKey = :key", Payment.class)
                .setParameter("key", key)
                .getResultStream().findFirst();
    }

    @Transactional
    public int updateStatus(Long paymentId, PaymentStatus status, LocalDateTime completedAt, String failReason) {
        return em.createQuery("update Payment p set p.status = :status, p.completedAt = :completedAt, p.failReason = :failReason where p.id = :id")
                .setParameter("status", status)
                .setParameter("completedAt", completedAt)
                .setParameter("failReason", failReason)
                .setParameter("id", paymentId)
                .executeUpdate();
    }

    @Transactional
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            em.persist(payment);
            return payment;
        } else {
            return em.merge(payment);
        }
    }
}
