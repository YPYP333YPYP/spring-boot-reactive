package com.example.reactive.domain.inventory_event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventService {

    private final InventoryEventRepository eventRepository;

    /**
     * 재고 이벤트 생성
     */
    public Mono<InventoryEvent> createEvent(
        Long inventoryId,
        InventoryEvent.EventType eventType,
        Double previousQuantity,
        Double newQuantity,
        String metadata) {

        InventoryEvent event = InventoryEvent.builder()
            .id(UUID.randomUUID().toString())
            .inventoryId(inventoryId)
            .eventType(eventType)
            .previousQuantity(previousQuantity)
            .newQuantity(newQuantity)
            .timestamp(LocalDateTime.now())
            .metadata(metadata)
            .build();

        return eventRepository.save(event)
            .doOnNext(savedEvent -> log.info("재고 이벤트 생성: 타입={}, 재고ID={}",
                savedEvent.getEventType(), savedEvent.getInventoryId()));
    }

    /**
     * 상품과 창고 정보가 포함된 이벤트 생성
     */
    public Mono<InventoryEvent> createEventWithDetails(
        Long inventoryId,
        Long productId,
        Long warehouseId,
        InventoryEvent.EventType eventType,
        Double previousQuantity,
        Double newQuantity,
        String metadata) {

        InventoryEvent event = InventoryEvent.builder()
            .id(UUID.randomUUID().toString())
            .inventoryId(inventoryId)
            .productId(productId)
            .warehouseId(warehouseId)
            .eventType(eventType)
            .previousQuantity(previousQuantity)
            .newQuantity(newQuantity)
            .timestamp(LocalDateTime.now())
            .metadata(metadata)
            .build();

        return eventRepository.save(event)
            .doOnNext(savedEvent -> log.info("상세 재고 이벤트 생성: 타입={}, 상품ID={}, 창고ID={}",
                savedEvent.getEventType(), savedEvent.getProductId(), savedEvent.getWarehouseId()));
    }

    /**
     * 재고별 이벤트 조회
     */
    public Flux<InventoryEvent> getEventsByInventory(Long inventoryId) {
        return eventRepository.findByInventoryIdOrderByTimestampDesc(inventoryId)
            .doOnNext(event -> log.debug("재고 이벤트 조회: {}", event.getEventType()));
    }

    /**
     * 상품별 이벤트 조회
     */
    public Flux<InventoryEvent> getEventsByProduct(Long productId) {
        return eventRepository.findByProductId(productId);
    }

    /**
     * 창고별 이벤트 조회
     */
    public Flux<InventoryEvent> getEventsByWarehouse(Long warehouseId) {
        return eventRepository.findByWarehouseId(warehouseId);
    }

    /**
     * 이벤트 타입별 조회
     */
    public Flux<InventoryEvent> getEventsByType(InventoryEvent.EventType eventType) {
        return eventRepository.findByEventType(eventType);
    }

    /**
     * 특정 기간 이벤트 조회
     */
    public Flux<InventoryEvent> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.findByTimestampBetween(startDate, endDate);
    }

    /**
     * 최근 이벤트 조회
     */
    public Flux<InventoryEvent> getRecentEvents(int limit) {
        return eventRepository.findRecentEvents(limit);
    }

    /**
     * 오늘 발생한 이벤트 조회
     */
    public Flux<InventoryEvent> getTodayEvents() {
        return eventRepository.findTodayEvents();
    }

    /**
     * 이벤트 통계 조회
     */
    public Mono<Map<String, Long>> getEventStatistics() {
        return eventRepository.countByEventType()
            .collectMap(
                row -> ((InventoryEvent.EventType) row[0]).name(),
                row -> (Long) row[1]
            );
    }

    /**
     * 상품의 최근 이벤트 조회
     */
    public Flux<InventoryEvent> getRecentEventsByProduct(Long productId, int limit) {
        return eventRepository.findRecentEventsByProduct(productId, limit);
    }

    /**
     * 입고 이벤트 생성 (편의 메서드)
     */
    public Mono<InventoryEvent> createStockAddedEvent(
        Long inventoryId,
        Long productId,
        Long warehouseId,
        Double previousQuantity,
        Double addedQuantity,
        String reason) {

        String metadata = String.format(
            "{\"reason\": \"%s\", \"addedQuantity\": %.2f, \"supplier\": \"%s\"}",
            reason, addedQuantity, "미정"
        );

        return createEventWithDetails(
            inventoryId, productId, warehouseId,
            InventoryEvent.EventType.STOCK_ADDED,
            previousQuantity,
            previousQuantity + addedQuantity,
            metadata
        );
    }

    /**
     * 출고 이벤트 생성 (편의 메서드)
     */
    public Mono<InventoryEvent> createStockRemovedEvent(
        Long inventoryId,
        Long productId,
        Long warehouseId,
        Double previousQuantity,
        Double removedQuantity,
        String reason) {

        String metadata = String.format(
            "{\"reason\": \"%s\", \"removedQuantity\": %.2f, \"customer\": \"%s\"}",
            reason, removedQuantity, "미정"
        );

        return createEventWithDetails(
            inventoryId, productId, warehouseId,
            InventoryEvent.EventType.STOCK_REMOVED,
            previousQuantity,
            previousQuantity - removedQuantity,
            metadata
        );
    }

    /**
     * 재고 이동 이벤트 생성 (편의 메서드)
     */
    public Mono<InventoryEvent> createStockMovedEvent(
        Long inventoryId,
        Long productId,
        Long warehouseId,
        Double quantity,
        String oldLocation,
        String newLocation,
        String reason) {

        String metadata = String.format(
            "{\"reason\": \"%s\", \"oldLocation\": \"%s\", \"newLocation\": \"%s\"}",
            reason, oldLocation, newLocation
        );

        return createEventWithDetails(
            inventoryId, productId, warehouseId,
            InventoryEvent.EventType.STOCK_MOVED,
            quantity,
            quantity,
            metadata
        );
    }

    /**
     * 임계값 알림 이벤트 생성 (편의 메서드)
     */
    public Mono<InventoryEvent> createThresholdAlertEvent(
        Long inventoryId,
        Long productId,
        Long warehouseId,
        Double currentQuantity,
        Double threshold,
        Long autoOrderId) {

        String metadata = String.format(
            "{\"alertLevel\": \"WARNING\", \"thresholdValue\": %.2f, \"autoOrderId\": %d, \"recommendedAction\": \"재입고 필요\"}",
            threshold, autoOrderId
        );

        return createEventWithDetails(
            inventoryId, productId, warehouseId,
            InventoryEvent.EventType.THRESHOLD_ALERT,
            currentQuantity,
            currentQuantity,
            metadata
        );
    }

    /**
     * 유통기한 알림 이벤트 생성 (편의 메서드)
     */
    public Mono<InventoryEvent> createExpiryAlertEvent(
        Long inventoryId,
        Long productId,
        Long warehouseId,
        Double quantity,
        LocalDateTime expiryDate,
        int daysRemaining) {

        String metadata = String.format(
            "{\"alertLevel\": \"%s\", \"expiryDate\": \"%s\", \"daysRemaining\": %d, \"recommendedAction\": \"%s\"}",
            daysRemaining <= 2 ? "URGENT" : "WARNING",
            expiryDate,
            daysRemaining,
            daysRemaining <= 2 ? "즉시 처리 필요" : "우선 출고 권장"
        );

        return createEventWithDetails(
            inventoryId, productId, warehouseId,
            InventoryEvent.EventType.EXPIRY_ALERT,
            quantity,
            quantity,
            metadata
        );
    }
}