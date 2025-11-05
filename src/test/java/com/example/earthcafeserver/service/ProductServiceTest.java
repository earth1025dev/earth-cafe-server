package com.example.earthcafeserver.service;

import com.example.earthcafeserver.dto.product.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    private ProductRequest createRequest() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("아메리카노");
        productRequest.setPrice(4000L);
        productRequest.setIsActive(true);
        List<ProductOptionRequest> options = new ArrayList<>();
        options.add(new ProductOptionRequest("ICE", 0L));
        options.add(new ProductOptionRequest("HOT", 0L));
        options.add(new ProductOptionRequest("샷 추가", 500L));
        productRequest.setOptions(options);

        return productRequest;
    }

    @Test
    void insertProduct() {
        // given
        ProductRequest request = createRequest();

        // when
        ProductSummaryResponse response = productService.insertProduct(request);

        // then
        assertNotNull(response.getId());
        assertEquals("아메리카노", response.getName());
        assertEquals(4000L, response.getPrice());
}

    @Test
    void updateProduct() {
        //given
        ProductRequest request = createRequest();
        ProductSummaryResponse saved = productService.insertProduct(request);

        ProductUpdateRequest updateRequest = new ProductUpdateRequest();
        updateRequest.setName("카라멜마끼야또");
        updateRequest.setPrice(8500L);

        // when
        ProductSummaryResponse productSummaryResponse = productService.updateProduct(saved.getId(), updateRequest);

        // then
        assertEquals("카라멜마끼야또", productSummaryResponse.getName());
        assertEquals(8500L, productSummaryResponse.getPrice());
    }

    @Test
    void deactivateProduct() {
        // given
        ProductRequest request = createRequest();
        ProductSummaryResponse saved = productService.insertProduct(request);

        // when
        ProductSummaryResponse productSummaryResponse = productService.deactivateProduct(saved.getId());

        // then
        assertEquals("아메리카노", productSummaryResponse.getName());
        assertEquals(false, productSummaryResponse.getIsActive());
    }

    @Test
    void activeProduct() {
        // given
        ProductRequest request = createRequest();
        ProductSummaryResponse saved = productService.insertProduct(request);
        ProductSummaryResponse response = productService.deactivateProduct(saved.getId());

        // when
        ProductSummaryResponse productSummaryResponse = productService.activeProduct(saved.getId());

        // then
        assertEquals("아메리카노", productSummaryResponse.getName());
        assertEquals(true, productSummaryResponse.getIsActive());
    }

    @Test
    void getProductById() {
        // given
        ProductRequest request = createRequest();
        ProductSummaryResponse saved = productService.insertProduct(request);

        // when
        ProductDetailResponse product = productService.getProductById(saved.getId());

        // then
        assertEquals("아메리카노", product.getName());

        List<ProductOptionRequest> options = new ArrayList<>();
        options.add(new ProductOptionRequest("ICE", 0L));
        options.add(new ProductOptionRequest("HOT", 0L));
        options.add(new ProductOptionRequest("샷 추가", 500L));
        assertThat(options).extracting("name", String.class).contains("HOT", "ICE", "샷 추가");
    }

    @Test
    void getActiveProductList() {
        // given
        ProductRequest request = createRequest();
        ProductSummaryResponse saved = productService.insertProduct(request);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("카라멜마까야또");
        productRequest.setPrice(8500L);
        productRequest.setIsActive(true);
        List<ProductOptionRequest> options = new ArrayList<>();
        options.add(new ProductOptionRequest("ICE", 0L));
        options.add(new ProductOptionRequest("HOT", 0L));
        options.add(new ProductOptionRequest("샷 추가", 500L));
        options.add(new ProductOptionRequest("카라멜 시럽 추가", 1000L));
        ProductSummaryResponse productSummaryResponse = productService.insertProduct(productRequest);

        // when
        List<ProductSummaryResponse> productList = productService.getActiveProductList();

        // then
        assertEquals(2, productList.size());
    }
}