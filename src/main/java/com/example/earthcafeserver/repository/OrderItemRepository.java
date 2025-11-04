package com.example.earthcafeserver.repository;

import com.example.earthcafeserver.domain.order.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemRepository {

    @PersistenceContext
    private EntityManager em;

    public List<OrderItem> findByOrderId(Long orderId) {
        return em.createQuery("select i from OrderItem i where i.order.id = :orderId", OrderItem.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderItem> findByProductId(Long productId) {
        return em.createQuery("select i from OrderItem i where i.product.id = :productId", OrderItem.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    public long countByOrderId(Long orderId) {
        return em.createQuery("select count(i) from OrderItem i where i.order.id = :orderId", Long.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }

    public Long sumLineAmountByOrderId(Long orderId) {
        return em.createQuery("select coalesce(sum(i.lineAmount), 0) from OrderItem i where i.order.id = :orderId", Long.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }

    @Transactional
    public OrderItem save(OrderItem orderItem) {
        if (orderItem.getId() == null) {
            em.persist(orderItem);
            return orderItem;
        } else {
            return em.merge(orderItem);
        }
    }

    /** cascade가 설정되지 않은 경우 직접 사용 */
    @Transactional
    public int deleteByOrderId(Long orderId) {
        return em.createQuery("delete from OrderItem oi where oi.order.id = :orderId")
                .setParameter("orderId", orderId)
                .executeUpdate();
    }
}
