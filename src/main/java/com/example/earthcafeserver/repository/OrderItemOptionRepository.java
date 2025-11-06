package com.example.earthcafeserver.repository;

import com.example.earthcafeserver.domain.order.OrderItemOption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemOptionRepository {

    @PersistenceContext
    private EntityManager em;

    public List<OrderItemOption> findByOrderItemId(Long orderItemId) {
        return em.createQuery("select oio from OrderItemOption oio where oio.orderItem.id = :orderItemId", OrderItemOption.class)
                .setParameter("orderItemId", orderItemId)
                .getResultList();
    }

    @Transactional
    public OrderItemOption save(OrderItemOption option) {
        if (option.getId() == null) {
            em.persist(option);
            return option;
        } else {
            return em.merge(option);
        }
    }
}
