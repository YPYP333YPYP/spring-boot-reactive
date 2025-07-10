package com.example.reactive.domain.inventory;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface InventoryRepository extends ReactiveCrudRepository<Inventory, Long> {

    // 창고별 재고 조회
    Flux<Inventory> findByWarehouseId(Long warehouseId);

    // 상품별 재고 조회
    Flux<Inventory> findByProductId(Long productId);

    // 특정 창고의 특정 상품 재고
    Mono<Inventory> findByWarehouseIdAndProductId(Long warehouseId, Long productId);

    // 임계값 이하 재고 조회 (자동 발주 대상)
    @Query("""
        SELECT * FROM inventory 
        WHERE quantity <= minimum_threshold 
        AND minimum_threshold > 0
        """)
    Flux<Inventory> findLowStockItems();

    // 유통기한 임박 재고 조회 (7일 이내)
    @Query("""
        SELECT * FROM inventory 
        WHERE expiry_date <= CURRENT_TIMESTAMP + INTERVAL '7 days' 
        AND expiry_date IS NOT NULL 
        AND quantity > 0
        ORDER BY expiry_date ASC
        """)
    Flux<Inventory> findItemsNearExpiry();

    // 창고별 임계값 이하 재고
    @Query("""
        SELECT * FROM inventory 
        WHERE warehouse_id = :warehouseId 
        AND quantity <= minimum_threshold 
        AND minimum_threshold > 0
        """)
    Flux<Inventory> findLowStockItemsByWarehouse(Long warehouseId);

    // 재발주 필요 상품 조회 (애플리케이션에서 함수 역할 대체)
    @Query("""
        SELECT i.*, p.product_name, p.unit, p.price, p.supplier,
               w.location as warehouse_location,
               (i.minimum_threshold * 3 - i.quantity) as suggested_quantity,
               ((i.minimum_threshold * 3 - i.quantity) * p.price) as estimated_cost
        FROM inventory i
        JOIN products p ON i.product_id = p.id
        JOIN warehouses w ON i.warehouse_id = w.id
        WHERE i.quantity <= i.minimum_threshold
        AND i.minimum_threshold > 0
        AND NOT EXISTS (
            SELECT 1 FROM purchase_orders po 
            WHERE po.product_id = i.product_id 
            AND po.warehouse_id = i.warehouse_id
            AND po.status IN ('PENDING', 'SENT', 'CONFIRMED', 'IN_TRANSIT')
        )
        ORDER BY (i.quantity / NULLIF(i.minimum_threshold, 1)) ASC
        """)
    Flux<Object[]> findItemsNeedReorder();

    // 유통기한 임박 상품 상세 정보
    @Query("""
        SELECT i.*, p.product_name, p.unit, w.location as warehouse_location,
               EXTRACT(DAY FROM (i.expiry_date - CURRENT_TIMESTAMP)) as days_until_expiry,
               CASE 
                   WHEN i.expiry_date <= CURRENT_TIMESTAMP + INTERVAL '2 days' THEN 'URGENT'
                   WHEN i.expiry_date <= CURRENT_TIMESTAMP + INTERVAL '5 days' THEN 'HIGH'
                   ELSE 'MEDIUM'
               END as priority_level
        FROM inventory i
        JOIN products p ON i.product_id = p.id
        JOIN warehouses w ON i.warehouse_id = w.id
        WHERE i.expiry_date IS NOT NULL
        AND i.expiry_date <= CURRENT_TIMESTAMP + INTERVAL '7 days'
        AND i.quantity > 0
        ORDER BY i.expiry_date ASC
        """)
    Flux<Object[]> findExpiringItemsWithDetails();

    // 재고 가치 계산
    @Query("""
        SELECT SUM(i.quantity * p.price) as total_value
        FROM inventory i
        JOIN products p ON i.product_id = p.id
        """)
    Mono<Double> calculateTotalInventoryValue();

    // 창고별 재고 통계
    @Query("""
        SELECT w.id, w.location,
               COUNT(i.id) as total_items,
               COUNT(CASE WHEN i.quantity <= i.minimum_threshold AND i.minimum_threshold > 0 THEN 1 END) as low_stock_items,
               COUNT(CASE WHEN i.quantity <= i.minimum_threshold * 0.5 AND i.minimum_threshold > 0 THEN 1 END) as critical_stock_items,
               COALESCE(SUM(i.quantity * p.price), 0) as total_value
        FROM warehouses w
        LEFT JOIN inventory i ON w.id = i.warehouse_id
        LEFT JOIN products p ON i.product_id = p.id
        GROUP BY w.id, w.location
        ORDER BY w.id
        """)
    Flux<Object[]> getWarehouseStatistics();
}
