package com.example.reactive.domain.product;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    // 상품명으로 검색 (부분 일치)
    @Query("SELECT * FROM products WHERE product_name ILIKE '%' || :productName || '%'")
    Flux<Product> findByProductNameContaining(String productName);

    // 카테고리별 조회
    Flux<Product> findByCategory(String category);

    // 공급업체별 조회
    Flux<Product> findBySupplier(String supplier);

    // 재고가 부족한 상품들 (currentStock < thresholdLevel)
    @Query("SELECT * FROM products WHERE current_stock < threshold_level")
    Flux<Product> findLowStockProducts();

    // 활성 상품들 (재고가 있는 상품)
    @Query("SELECT * FROM products WHERE current_stock > 0")
    Flux<Product> findActiveProducts();

    // 가격 범위로 조회
    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice")
    Flux<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // 최근 사용된 상품들 (최근 30일 내)
    @Query("SELECT * FROM products WHERE last_used_at >= CURRENT_DATE - INTERVAL '30 days' ORDER BY last_used_at DESC")
    Flux<Product> findRecentlyUsedProducts();

    // 카테고리별 개수
    @Query("SELECT category, COUNT(*) as count FROM products GROUP BY category")
    Flux<Object[]> countByCategory();
}
