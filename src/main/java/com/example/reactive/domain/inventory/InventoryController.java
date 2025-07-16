package com.example.reactive.domain.inventory;

import com.example.reactive.domain.order.AutoOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final AutoOrderService autoOrderService;

    /**
     * 재고 입고 처리
     */
    @PostMapping("/{inventoryId}/add-stock")
    public Mono<ResponseEntity<Inventory>> addStock(
        @PathVariable Long inventoryId,
        @RequestParam Double quantity,
        @RequestParam(defaultValue = "수동 입고") String reason) {

        return inventoryService.addStock(inventoryId, quantity, reason)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 재고 출고 처리
     */
    @PostMapping("/{inventoryId}/remove-stock")
    public Mono<ResponseEntity<Inventory>> removeStock(
        @PathVariable Long inventoryId,
        @RequestParam Double quantity,
        @RequestParam(defaultValue = "수동 출고") String reason) {

        return inventoryService.removeStock(inventoryId, quantity, reason)
            .map(ResponseEntity::ok)
            .onErrorReturn(ResponseEntity.badRequest().build());
    }

    /**
     * 재고 이동 처리
     */
    @PostMapping("/{inventoryId}/move")
    public Mono<ResponseEntity<Inventory>> moveStock(
        @PathVariable Long inventoryId,
        @RequestParam String newLocation,
        @RequestParam(defaultValue = "재고 이동") String reason) {

        return inventoryService.moveStock(inventoryId, newLocation, reason)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 창고별 재고 현황 조회
     */
    @GetMapping("/warehouse/{warehouseId}")
    public Flux<Inventory> getWarehouseInventory(@PathVariable Long warehouseId) {
        return inventoryService.getWarehouseInventory(warehouseId);
    }

    /**
     * 임계값 이하 재고 목록
     */
    @GetMapping("/low-stock")
    public Flux<Inventory> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }

    /**
     * 수동 자동 발주 트리거
     */
    @PostMapping("/trigger-auto-orders")
    public Flux<Object> triggerAutoOrders() {
        return autoOrderService.checkAndCreateAutoOrders()
            .map(order -> new Object() {
                public final Long orderId = order.getId();
                public final Long productId = order.getProductId();
                public final Double quantity = order.getRequestedQuantity();
                public final String status = "자동 발주 생성됨";
            });
    }
}
