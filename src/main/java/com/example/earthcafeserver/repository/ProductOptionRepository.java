package com.example.earthcafeserver.repository;

import com.example.earthcafeserver.domain.product.ProductOption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductOptionRepository {

    @PersistenceContext
    private EntityManager em;

    public List<ProductOption> findByProductId(Long productId) {
        return em.createQuery("select o from ProductOption o where o.product.id = :productId", ProductOption.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    public Optional<ProductOption> findByProductIdAndProductOptionId(Long productId, Long productOptionId) {
        return em.createQuery("select o from ProductOption o where o.product.id = :productId and o.id = :productOptionId", ProductOption.class)
                .setParameter("productId", productId)
                .setParameter("productOptionId", productOptionId)
                .getResultList().stream().findFirst();
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
