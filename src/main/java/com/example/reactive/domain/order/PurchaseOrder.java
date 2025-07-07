package com.example.reactive.domain.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("purchase_orders")
public class PurchaseOrder {
    @Id
    private Long id;

    @Column("supplier_id")
    private Long supplierId; // 매입처

    @Column("product_id")
    private Long productId;

    @Column("warehouse_id")
    private Long warehouseId;

    @Column("requested_by")
    private Long requestedBy; // 발주 요청자

    private Double requestedQuantity; // 발주 요청 수량
    private Double unitPrice; // 단가
    private Double totalAmount; // 총 금액

    private OrderStatus status; // 발주 상태
    private OrderType orderType; // 발주 유형 (자동/수동)

    private String notes; // 특이사항
    private LocalDateTime requestedAt; // 발주 요청일
    private LocalDateTime expectedDeliveryDate; // 예상 납품일
    private LocalDateTime actualDeliveryDate; // 실제 납품일
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum OrderStatus {
        PENDING,      // 발주 대기
        SENT,         // 발주 전송됨
        CONFIRMED,    // 공급업체 확인
        IN_TRANSIT,   // 배송 중
        DELIVERED,    // 납품 완료
        CANCELLED     // 취소
    }

    public enum OrderType {
        AUTOMATIC,    // 자동 발주 (임계값 도달)
        MANUAL,       // 수동 발주
        EMERGENCY     // 긴급 발주
    }
}