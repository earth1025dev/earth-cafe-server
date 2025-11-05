package com.example.earthcafeserver.repository;

import com.example.earthcafeserver.domain.product.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Product> findById(Long id) {
        return em.createQuery("select p from Product p where p.id = :id", Product.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    public Optional<Product> findByName(String name) {
        return em.createQuery("select p from Product p where p.name = :name", Product.class)
                .setParameter("name", name)
                .getResultStream().findFirst();
    }

    public List<Product> findActiveProducts(Boolean isActive) {
        return em.createQuery("select p from Product where p.isActive = :isActive", Product.class)
                .setParameter("isActive", isActive)
                .getResultList();
    }

    @Transactional
    public int updateIsActive(Long productId, boolean active) {
        return em.createQuery("update Product p set p.isActive = :active where p.id = :id")
                .setParameter("active", active)
                .setParameter("id", productId)
                .executeUpdate();
    }

    @Transactional
    public Product save(Product product) {
        if (product.getId() == null) {
            em.persist(product);
            return product;
        } else {
            return em.merge(product);
        }
    }
}
