package com.example.reactive.domain.inventory_event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {
    private String id;

    @Column("inventory_id")
    private Long inventoryId;

    @Column("warehouse_id")
    private Long productId;

    @Column("warehouse_id")
    private Long warehouseId;

    private EventType eventType; // 이벤트 분류
    private Double previousQuantity; // 이전 재고 수량
    private Double newQuantity; // 이후 재고 수량
    private LocalDateTime timestamp; // 이벤트 발생일

    private String metadata; // 이벤트 상세 내용 (JSONB 타입을 String 타입으로 저장)


    public enum EventType {
        STOCK_ADDED,  // 재고 추가
        STOCK_REMOVED, // 재고 제거
        THRESHOLD_ALERT, // 재고 임계치 알림
        EXPIRY_ALERT, // 유통기한 알림
        STOCK_MOVED // 재고 이동
    }

    /**
     * 이벤트 타입별 메타데이터 JSON 구조 예시:
     *
     * 1. STOCK_ADDED - 재고 추가 이벤트
     * {
     *   "reason": "정기 입고",
     *   "supplierName": "SUP-001",
     *   "notes": "특이사항 없음"
     * }
     *
     * 2. STOCK_REMOVED - 재고 제거 이벤트
     * {
     *   "reason": "주문 출고",
     *   "removedBy": "창고관리자",
     *   "notes": "고객 요청에 따른 긴급 출고"
     * }
     *
     * 3. STOCK_MOVED - 재고 이동 이벤트
     * {
     *   "reason": "창고 최적화",
     *   "destinationLocation": "선반-8",
     * }
     *
     * 4. THRESHOLD_ALERT - 재고 임계치 알림 이벤트
     * {
     *   "alertLevel": "WARNING",
     *   "thresholdValue": 10.0,
     *   "recommendedOrderQuantity": 100,
     *   "notes": "빠른 재입고 필요"
     * }
     *
     * 5. EXPIRY_ALERT - 유통기한 알림 이벤트
     * {
     *   "alertLevel": "WARNING",
     *   "expiryDate": "2023-05-15",
     *   "daysRemaining": 14,
     *   "recommendedAction": "우선 출고 표시"
     * }
     */
}
