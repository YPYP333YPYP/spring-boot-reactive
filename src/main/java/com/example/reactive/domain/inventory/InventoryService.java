package com.example.reactive.domain.inventory;

import com.example.reactive.domain.inventory_event.InventoryEvent;
import com.example.reactive.domain.inventory_event.InventoryEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventService eventService;

    /**
     * 재고 찾기 또는 생성 (발주 납품 시 사용)
     */
    public Mono<Inventory> findOrCreateInventory(Long warehouseId, Long productId) {
        return inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId)
            .switchIfEmpty(createNewInventory(warehouseId, productId));
    }

    /**
     * 새 재고 아이템 생성
     */
    private Mono<Inventory> createNewInventory(Long warehouseId, Long productId) {
        Inventory newInventory = Inventory.builder()
            .warehouseId(warehouseId)
            .productId(productId)
            .location("미지정")
            .quantity(0.0)
            .minimumThreshold(10.0) // 기본 임계값
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        return inventoryRepository.save(newInventory)
            .doOnNext(inventory -> log.info("새 재고 아이템 생성: 창고={}, 상품={}",
                warehouseId, productId));
    }

    /**
     * 재고 입고 처리
     */
    @Transactional
    public Mono<Inventory> addStock(Long inventoryId, Double quantity, String reason) {
        return inventoryRepository.findById(inventoryId)
            .flatMap(inventory -> {
                Double previousQuantity = inventory.getQuantity();
                Double newQuantity = previousQuantity + quantity;

                inventory.setQuantity(newQuantity);
                inventory.setUpdatedAt(LocalDateTime.now());

                return inventoryRepository.save(inventory)
                    .flatMap(savedInventory -> {
                        String metadata = String.format("{\"reason\": \"%s\", \"addedQuantity\": %.2f}",
                            reason, quantity);

                        return eventService.createEventWithDetails(
                            inventoryId,
                            inventory.getProductId(),
                            inventory.getWarehouseId(),
                            InventoryEvent.EventType.STOCK_ADDED,
                            previousQuantity,
                            newQuantity,
                            metadata
                        ).then(Mono.just(savedInventory));
                    });
            });
    }

    /**
     * 재고 출고 처리
     */
    @Transactional
    public Mono<Inventory> removeStock(Long inventoryId, Double quantity, String reason) {
        return inventoryRepository.findById(inventoryId)
            .flatMap(inventory -> {
                Double previousQuantity = inventory.getQuantity();
                Double newQuantity = previousQuantity - quantity;

                if (newQuantity < 0) {
                    return Mono.error(new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + previousQuantity + ", 요청 수량: " + quantity));
                }

                inventory.setQuantity(newQuantity);
                inventory.setUpdatedAt(LocalDateTime.now());

                return inventoryRepository.save(inventory)
                    .flatMap(savedInventory -> {
                        String metadata = String.format("{\"reason\": \"%s\", \"removedQuantity\": %.2f}",
                            reason, quantity);

                        return eventService.createEventWithDetails(
                            inventoryId,
                            inventory.getProductId(),
                            inventory.getWarehouseId(),
                            InventoryEvent.EventType.STOCK_REMOVED,
                            previousQuantity,
                            newQuantity,
                            metadata
                        ).then(Mono.just(savedInventory));
                    });
            });
    }

    /**
     * 재고 이동 처리
     */
    @Transactional
    public Mono<Inventory> moveStock(Long inventoryId, String newLocation, String reason) {
        return inventoryRepository.findById(inventoryId)
            .flatMap(inventory -> {
                String oldLocation = inventory.getLocation();
                inventory.setLocation(newLocation);
                inventory.setUpdatedAt(LocalDateTime.now());

                return inventoryRepository.save(inventory)
                    .flatMap(savedInventory -> {
                        String metadata = String.format(
                            "{\"reason\": \"%s\", \"oldLocation\": \"%s\", \"newLocation\": \"%s\"}",
                            reason, oldLocation, newLocation);

                        return eventService.createEventWithDetails(
                            inventoryId,
                            inventory.getProductId(),
                            inventory.getWarehouseId(),
                            InventoryEvent.EventType.STOCK_MOVED,
                            inventory.getQuantity(),
                            inventory.getQuantity(),
                            metadata
                        ).then(Mono.just(savedInventory));
                    });
            });
    }

    /**
     * 창고별 재고 현황 조회
     */
    public Flux<Inventory> getWarehouseInventory(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId);
    }

    /**
     * 임계값 이하 재고 목록 조회
     */
    public Flux<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }
}
