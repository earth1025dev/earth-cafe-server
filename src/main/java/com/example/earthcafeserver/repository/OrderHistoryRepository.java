package com.example.earthcafeserver.repository;

import com.example.earthcafeserver.domain.order.OrderHistory;
import com.example.earthcafeserver.domain.order.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderHistoryRepository {

    @PersistenceContext
    private EntityManager em;

    public List<OrderHistory> findByOrderId(Long orderId) {
        return em.createQuery("select oh from OrderHistory oh where oh.order.id = :orderId order by oh.changedAt asc", OrderHistory.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderHistory> findRecentByOrderId(Long orderId, int limit) {
        return em.createQuery("select oh from OrderHistory oh where oh.order.id = :orderId order by oh.changedAt asc", OrderHistory.class)
                .setParameter("orderId", orderId)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<OrderHistory> findByOrderIdAndStatus(Long orderId, OrderStatus status) {
        return em.createQuery("select oh from OrderHistory oh where oh.order.id = :orderId and oh.newStatus = :status order by oh.changedAt asc", OrderHistory.class)
                .setParameter("orderId", orderId)
                .setParameter("status", status)
                .getResultList();
    }

    @Transactional
    public OrderHistory save(OrderHistory history) {
        if (history.getId() == null) {
            em.persist(history);
            return history;
        } else {
            return em.merge(history);
        }
    }
}
