package com.example.earthcafeserver.repository;

import com.example.earthcafeserver.domain.product.ProductOption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductOptionRepository {

    @PersistenceContext
    private EntityManager em;

    public List<ProductOption> findByProductId(Long productId) {
        return em.createQuery("select o from ProductOption where o.product.id = :productId", ProductOption.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    @Transactional
    public ProductOption save(ProductOption option) {
        if (option.getId() == null) {
            em.persist(option);
            return option;
        } else {
            return em.merge(option);
        }
    }
}
