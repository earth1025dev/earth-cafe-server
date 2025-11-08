package com.example.earthcafeserver.controller;

import com.example.earthcafeserver.dto.product.ProductOptionRequest;
import com.example.earthcafeserver.dto.product.ProductRequest;
import com.example.earthcafeserver.dto.product.ProductUpdateRequest;
import com.example.earthcafeserver.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ProductService productService;

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
    @DisplayName("상품 등록 성공")
    void addProduct_success() throws Exception {
        ProductRequest request = createRequest();

        // 1) 상품 등록
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("아메리카노"))
                .andExpect(jsonPath("$.price").value(4000L));
    }

    @Test
    @DisplayName("상품 수정 성공")
    void editProduct_success() throws Exception {
        ProductRequest addRequest = createRequest();

        // 1) 상품 등록
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(addRequest)))
                .andReturn().getResponse().getContentAsString();
        Long productId = mapper.readTree(response).get("id").asLong();

        ProductUpdateRequest updateRequest = new ProductUpdateRequest();
        updateRequest.setPrice(8500L);
        updateRequest.setName("카페라떼");

        // 2) 상품 수정
        mockMvc.perform(put("/api/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("카페라떼"))
                .andExpect(jsonPath("$.price").value(8500L));
    }

    @Test
    @DisplayName("상품 비활성화 성공")
    void deactivateProduct_success() throws Exception {
        ProductRequest addRequest = createRequest();

        // 1) 상품 등록
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(addRequest)))
                .andReturn().getResponse().getContentAsString();
        Long productId = mapper.readTree(response).get("id").asLong();

        // 2) 상품 비활성화
        mockMvc.perform(patch("/api/products/{productId}/deactivate", productId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("상품 활성화 성공")
    void activateProduct_success() throws Exception {
        ProductRequest addRequest = createRequest();

        // 1) 상품 등록
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(addRequest)))
                .andReturn().getResponse().getContentAsString();
        Long productId = mapper.readTree(response).get("id").asLong();

        // 2) 상품 비활성화
        mockMvc.perform(patch("/api/products/{productId}/deactivate", productId))
                .andExpect(status().isNoContent());

        // 3) 상품 재활성화
        mockMvc.perform(patch("/api/products/{productId}/activate", productId))
                .andExpect(status().isNoContent());
    }
}