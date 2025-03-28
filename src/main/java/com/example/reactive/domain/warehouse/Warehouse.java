package com.example.reactive.domain.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("warehouses")
public class Warehouse {
    @Id
    private Long id;

    @Column("warehouse_type")
    private WarehouseType warehouseType; // 창고 분류
    private String location; // 위치
    private String description; // 설명

    public enum WarehouseType {
        REFRIGERATED, // 냉장
        FROZEN,       // 냉동
        DRY_GOODS     // 공산품
    }
}
