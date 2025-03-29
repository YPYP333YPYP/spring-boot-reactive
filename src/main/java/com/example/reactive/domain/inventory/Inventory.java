package com.example.reactive.domain.inventory;

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
@Table("inventory")
public class Inventory {
    @Id
    private Long id;

    @Column("warehouse_id")
    private Long warehouseId;

    @Column("product_id")
    private Long productId;

    private String location; // 창고 내 위치 (구역, 선반 등)
    private Double quantity; // 재고 수량
    private Double minimumThreshold; // 임계값
    private LocalDateTime expiryDate; // 재고 부족 해당 날짜
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}