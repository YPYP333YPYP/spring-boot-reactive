package com.example.reactive.domain.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 생성
     */
    @Transactional
    public Mono<Product> createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product)
            .doOnNext(savedProduct -> log.info("상품 생성: ID={}, 이름={}",
                savedProduct.getId(), savedProduct.getProductName()));
    }

    /**
     * 상품 수정
     */
    @Transactional
    public Mono<Product> updateProduct(Long productId, Product updatedProduct) {
        return productRepository.findById(productId)
            .flatMap(existingProduct -> {
                existingProduct.setProductName(updatedProduct.getProductName());
                existingProduct.setUnit(updatedProduct.getUnit());
                existingProduct.setPrice(updatedProduct.getPrice());
                existingProduct.setCategory(updatedProduct.getCategory());
                existingProduct.setSupplier(updatedProduct.getSupplier());
                existingProduct.setUpdatedAt(LocalDateTime.now());

                return productRepository.save(existingProduct);
            })
            .doOnNext(savedProduct -> log.info("상품 수정: ID={}", savedProduct.getId()));
    }

    /**
     * 상품 조회
     */
    public Mono<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    /**
     * 모든 상품 조회
     */
    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 상품명으로 검색
     */
    public Flux<Product> searchProducts(String productName) {
        return productRepository.findByProductNameContaining(productName);
    }

    /**
     * 카테고리별 상품 조회
     */
    public Flux<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /**
     * 공급업체별 상품 조회
     */
    public Flux<Product> getProductsBySupplier(String supplier) {
        return productRepository.findBySupplier(supplier);
    }

    /**
     * 재고 부족 상품 조회
     */
    public Flux<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    /**
     * 활성 상품 조회
     */
    public Flux<Product> getActiveProducts() {
        return productRepository.findActiveProducts();
    }

    /**
     * 최근 사용된 상품 조회
     */
    public Flux<Product> getRecentlyUsedProducts() {
        return productRepository.findRecentlyUsedProducts();
    }

    /**
     * 카테고리별 통계
     */
    public Mono<Map<String, Long>> getCategoryStatistics() {
        return productRepository.countByCategory()
            .collectMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            );
    }

    /**
     * 상품 삭제
     */
    @Transactional
    public Mono<Void> deleteProduct(Long productId) {
        return productRepository.deleteById(productId)
            .doOnSuccess(unused -> log.info("상품 삭제: ID={}", productId));
    }
}
