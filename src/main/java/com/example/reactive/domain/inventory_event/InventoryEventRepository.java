package com.example.reactive.domain.inventory_event;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Repository
public interface InventoryEventRepository extends ReactiveCrudRepository<InventoryEvent, String> {

    // 재고별 이벤트 조회 (최신순)
    Flux<InventoryEvent> findByInventoryIdOrderByTimestampDesc(Long inventoryId);

    // 이벤트 타입별 조회
    Flux<InventoryEvent> findByEventType(InventoryEvent.EventType eventType);

    // 상품별 이벤트 조회
    Flux<InventoryEvent> findByProductId(Long productId);

    // 창고별 이벤트 조회
    Flux<InventoryEvent> findByWarehouseId(Long warehouseId);

    // 특정 기간 이벤트 조회
    @Query("SELECT * FROM inventory_events WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    Flux<InventoryEvent> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 최근 이벤트 조회
    @Query("SELECT * FROM inventory_events ORDER BY timestamp DESC LIMIT :limit")
    Flux<InventoryEvent> findRecentEvents(int limit);

    // 특정 재고의 특정 타입 이벤트
    Flux<InventoryEvent> findByInventoryIdAndEventType(Long inventoryId, InventoryEvent.EventType eventType);

    // 오늘 발생한 이벤트들
    @Query("SELECT * FROM inventory_events WHERE DATE(timestamp) = CURRENT_DATE ORDER BY timestamp DESC")
    Flux<InventoryEvent> findTodayEvents();

    // 이벤트 통계 (타입별 개수)
    @Query("SELECT event_type, COUNT(*) as count FROM inventory_events GROUP BY event_type")
    Flux<Object[]> countByEventType();

    // 특정 상품의 최근 이벤트
    @Query("SELECT * FROM inventory_events WHERE product_id = :productId ORDER BY timestamp DESC LIMIT :limit")
    Flux<InventoryEvent> findRecentEventsByProduct(Long productId, int limit);
}