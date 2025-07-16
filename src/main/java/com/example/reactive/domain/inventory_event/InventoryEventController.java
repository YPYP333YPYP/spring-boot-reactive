package com.example.reactive.domain.inventory_event;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory-events")
@RequiredArgsConstructor
public class InventoryEventController {

    private final InventoryEventService eventService;

    /**
     * 재고별 이벤트 조회
     */
    @GetMapping("/inventory/{inventoryId}")
    public Flux<InventoryEvent> getEventsByInventory(@PathVariable Long inventoryId) {
        return eventService.getEventsByInventory(inventoryId);
    }

    /**
     * 상품별 이벤트 조회
     */
    @GetMapping("/product/{productId}")
    public Flux<InventoryEvent> getEventsByProduct(@PathVariable Long productId) {
        return eventService.getEventsByProduct(productId);
    }

    /**
     * 창고별 이벤트 조회
     */
    @GetMapping("/warehouse/{warehouseId}")
    public Flux<InventoryEvent> getEventsByWarehouse(@PathVariable Long warehouseId) {
        return eventService.getEventsByWarehouse(warehouseId);
    }

    /**
     * 이벤트 타입별 조회
     */
    @GetMapping("/type/{eventType}")
    public Flux<InventoryEvent> getEventsByType(@PathVariable InventoryEvent.EventType eventType) {
        return eventService.getEventsByType(eventType);
    }

    /**
     * 특정 기간 이벤트 조회
     */
    @GetMapping("/date-range")
    public Flux<InventoryEvent> getEventsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return eventService.getEventsByDateRange(startDate, endDate);
    }

    /**
     * 최근 이벤트 조회
     */
    @GetMapping("/recent")
    public Flux<InventoryEvent> getRecentEvents(@RequestParam(defaultValue = "50") int limit) {
        return eventService.getRecentEvents(limit);
    }

    /**
     * 오늘 발생한 이벤트 조회
     */
    @GetMapping("/today")
    public Flux<InventoryEvent> getTodayEvents() {
        return eventService.getTodayEvents();
    }

    /**
     * 이벤트 통계 조회
     */
    @GetMapping("/statistics")
    public Mono<Map<String, Long>> getEventStatistics() {
        return eventService.getEventStatistics();
    }

    /**
     * 상품의 최근 이벤트 조회
     */
    @GetMapping("/product/{productId}/recent")
    public Flux<InventoryEvent> getRecentEventsByProduct(
        @PathVariable Long productId,
        @RequestParam(defaultValue = "10") int limit) {

        return eventService.getRecentEventsByProduct(productId, limit);
    }
}