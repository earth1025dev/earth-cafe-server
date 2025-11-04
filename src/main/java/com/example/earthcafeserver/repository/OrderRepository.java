package com.example.earthcafeserver.repository;

import com.example.earthcafeserver.domain.order.Order;
import com.example.earthcafeserver.domain.order.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Order> findById(Long orderId) {
        return em.createQuery("select o from Order o where o.id = :id", Order.class)
                .setParameter("id", orderId)
                .getResultStream().findFirst();
    }

    public List<Order> findByStatus(OrderStatus status) {
        return em.createQuery("select o from Order o where o.status = :status", Order.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Order> findByMemberId(Long memberId) {
        return em.createQuery("select o from Order o where o.member.id = :memberId", Order.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Transactional
    public int updateStatus(Long orderId, OrderStatus status) {
        int updated = em.createQuery("update Order o set o.status = :status where o.id = :id")
                .setParameter("status", status)
                .setParameter("id", orderId)
                .executeUpdate();

        if (updated == 0) {
            throw new IllegalArgumentException("존재하지 않는 주문입니다. id=" + orderId);
        }

        return updated;
    }

    @Transactional
    public Order save(Order order) {
        if (order.getId() == null) {
            em.persist(order);
            return order;
        } else {
            return em.merge(order);
        }
    }
}
