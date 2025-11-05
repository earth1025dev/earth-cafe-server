package com.example.earthcafeserver.service;

import com.example.earthcafeserver.domain.product.Product;
import com.example.earthcafeserver.domain.product.ProductOption;
import com.example.earthcafeserver.dto.product.*;
import com.example.earthcafeserver.repository.ProductOptionRepository;
import com.example.earthcafeserver.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductOptionRepository productOptionRepository;

    public ProductSummaryResponse insertProduct(ProductRequest request) {
        Product product = productRepository.save(new Product(request.getName(), request.getPrice()));
        List<ProductOptionRequest> options = request.getOptions();
        if (options != null) {
            for (ProductOptionRequest o : options) {
                ProductOption option = new ProductOption(product, o.getName(), o.getExtraPrice());
                productOptionRepository.save(option);
                product.addOption(option);
            }
        }

        return ProductSummaryResponse.from(product);
    }

    public ProductSummaryResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        return ProductSummaryResponse.from(product);
    }

    public ProductSummaryResponse deactivateProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        product.setIsActive(false);

        return ProductSummaryResponse.from(product);
    }

    public ProductSummaryResponse activeProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        product.setIsActive(true);

        return ProductSummaryResponse.from(product);
    }

    public ProductDetailResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        return ProductDetailResponse.from(product);
    }

    public List<ProductSummaryResponse> getActiveProductList() {
        List<Product> activeProducts = productRepository.findActiveProducts(true);

        return activeProducts.stream().map(ProductSummaryResponse::from).toList();
    }

}
