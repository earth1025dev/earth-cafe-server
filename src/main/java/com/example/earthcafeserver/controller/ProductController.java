package com.example.earthcafeserver.controller;

import com.example.earthcafeserver.dto.product.ProductDetailResponse;
import com.example.earthcafeserver.dto.product.ProductRequest;
import com.example.earthcafeserver.dto.product.ProductSummaryResponse;
import com.example.earthcafeserver.dto.product.ProductUpdateRequest;
import com.example.earthcafeserver.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록
     *
     * @param request
     * @return
     */
    @Operation(summary = "상품 등록", description = "상품을 등록합니다.")
    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest request) {
        ProductSummaryResponse response = productService.insertProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 상품 단건 조회
     *
     * @param productId
     * @return
     */
    @Operation(summary = "상품 단건 조회", description = "상품 번호 기준 단건을 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        ProductDetailResponse product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    /**
     * 활성화된 상품 전체 조회
     *
     * @param isActive
     * @return
     */
    @Operation(summary = "활성화된 상품 전체 조회", description = "활성화된 모든 상품을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getActiveProducts(@RequestParam(name = "active", required = false, defaultValue = "true") boolean isActive) {
        List<ProductSummaryResponse> productList = productService.getActiveProductList();
        return ResponseEntity.ok(productList);
    }

    /**
     * 상품 수정
     *
     * @param productId
     * @param request
     * @return
     */
    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    @PutMapping("/{productId}")
    public ResponseEntity<?> editProduct(@PathVariable Long productId, @RequestBody ProductUpdateRequest request) {
        ProductSummaryResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 비활성화(판매 중단)
     *
     * @param productId
     * @return
     */
    @Operation(summary = "상품 비활성화(판매 중단)", description = "상품을 판매 중단합니다.")
    @PatchMapping("/{productId}/deactivate")
    public ResponseEntity<?> deactivateProduct(@PathVariable Long productId) {
        productService.deactivateProduct(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 활성화(재판매)
     * @param productId
     * @return
     */
    @Operation(summary = "상품 활성화(재판매)", description = "상품을 재판매합니다.")
    @PatchMapping("/{productId}/activate")
    public ResponseEntity<?> activateProduct(@PathVariable Long productId) {
        productService.activeProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
