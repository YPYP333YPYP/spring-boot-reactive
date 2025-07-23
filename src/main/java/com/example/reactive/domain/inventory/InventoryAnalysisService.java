package com.example.reactive.domain.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryAnalysisService {

    private final InventoryRepository inventoryRepository;

    /**
     * 재발주 필요 상품 조회 (PostgreSQL 함수 대체)
     */
    public Flux<ReorderItem> getItemsNeedReorder() {
        return inventoryRepository.findItemsNeedReorder()
            .map(row -> ReorderItem.builder()
                .inventoryId((Long) row[0])
                .productName((String) row[1])
                .currentQuantity((Double) row[2])
                .minimumThreshold((Double) row[3])
                .suggestedOrderQuantity((Double) row[4])
                .warehouseLocation((String) row[5])
                .supplierName((String) row[6])
                .estimatedCost((Double) row[7])
                .build())
            .doOnNext(item -> log.debug("재발주 필요 상품: {}", item.getProductName()));
    }

    /**
     * 유통기한 임박 상품 조회 (PostgreSQL 함수 대체)
     */
    public Flux<ExpiringItem> getExpiringItems() {
        return inventoryRepository.findExpiringItemsWithDetails()
            .map(row -> ExpiringItem.builder()
                .inventoryId((Long) row[0])
                .productName((String) row[1])
                .quantity((Double) row[2])
                .expiryDate((java.time.LocalDateTime) row[3])
                .daysUntilExpiry((Integer) row[4])
                .warehouseLocation((String) row[5])
                .inventoryLocation((String) row[6])
                .priorityLevel((String) row[7])
                .build())
            .doOnNext(item -> log.debug("유통기한 임박 상품: {}", item.getProductName()));
    }

    /**
     * 총 재고 가치 계산
     */
    public Mono<Double> getTotalInventoryValue() {
        return inventoryRepository.calculateTotalInventoryValue()
            .defaultIfEmpty(0.0);
    }

    // DTO 클래스들
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReorderItem {
        private Long inventoryId;
        private String productName;
        private Double currentQuantity;
        private Double minimumThreshold;
        private Double suggestedOrderQuantity;
        private String warehouseLocation;
        private String supplierName;
        private Double estimatedCost;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExpiringItem {
        private Long inventoryId;
        private String productName;
        private Double quantity;
        private java.time.LocalDateTime expiryDate;
        private Integer daysUntilExpiry;
        private String warehouseLocation;
        private String inventoryLocation;
        private String priorityLevel;
    }
}
