package com.example.reactive.domain.product;

import java.time.LocalDateTime;
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
@Table("products")
public class Product {
    @Id
    private Long id;

    @Column("product_name")
    private String productName;           // 제품 이름
    private String unit;           // 단위
    private Double price;          // 가격
    private String category;       // 카테고리
    private String supplier;       // 공급업체
    private Integer currentStock;  // 현재 재고수량
    private LocalDateTime lastUsedAt; // 최근 사용일
    private LocalDateTime createdAt; // 생성일
    private LocalDateTime updatedAt; // 수정일
}
