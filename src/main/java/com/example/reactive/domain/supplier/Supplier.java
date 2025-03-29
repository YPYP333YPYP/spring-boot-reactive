package com.example.reactive.domain.supplier;

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
@Table("suppliers")
public class Supplier {
    @Id
    private Long id;

    @Column("supplier_name")
    private String supplierName;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String taxId;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}