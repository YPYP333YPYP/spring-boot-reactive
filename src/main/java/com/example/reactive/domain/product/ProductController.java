package com.example.reactive.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 생성
     */
    @PostMapping
    public Mono<ResponseEntity<Product>> createProduct(@RequestBody Product product) {
        return productService.createProduct(product)
            .map(ResponseEntity::ok);
    }

    /**
     * 상품 수정
     */
    @PutMapping("/{productId}")
    public Mono<ResponseEntity<Product>> updateProduct(
        @PathVariable Long productId,
        @RequestBody Product product) {

        return productService.updateProduct(productId, product)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 상품 조회
     */
    @GetMapping("/{productId}")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable Long productId) {
        return productService.getProductById(productId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 모든 상품 조회
     */
    @GetMapping
    public Flux<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * 상품 검색
     */
    @GetMapping("/search")
    public Flux<Product> searchProducts(@RequestParam String name) {
        return productService.searchProducts(name);
    }

    /**
     * 카테고리별 상품 조회
     */
    @GetMapping("/category/{category}")
    public Flux<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }

    /**
     * 공급업체별 상품 조회
     */
    @GetMapping("/supplier/{supplier}")
    public Flux<Product> getProductsBySupplier(@PathVariable String supplier) {
        return productService.getProductsBySupplier(supplier);
    }

    /**
     * 재고 부족 상품 조회
     */
    @GetMapping("/low-stock")
    public Flux<Product> getLowStockProducts() {
        return productService.getLowStockProducts();
    }

    /**
     * 활성 상품 조회 (재고가 있는 상품)
     */
    @GetMapping("/active")
    public Flux<Product> getActiveProducts() {
        return productService.getActiveProducts();
    }

    /**
     * 최근 사용된 상품 조회
     */
    @GetMapping("/recently-used")
    public Flux<Product> getRecentlyUsedProducts() {
        return productService.getRecentlyUsedProducts();
    }

    /**
     * 카테고리별 통계
     */
    @GetMapping("/statistics/category")
    public Mono<Map<String, Long>> getCategoryStatistics() {
        return productService.getCategoryStatistics();
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/{productId}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long productId) {
        return productService.deleteProduct(productId)
            .then(Mono.just(ResponseEntity.ok().<Void>build()))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
