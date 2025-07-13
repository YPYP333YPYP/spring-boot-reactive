package com.example.reactive.domain.supplier;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SupplierRepository extends ReactiveCrudRepository<Supplier, Long> {

    // 공급업체명으로 조회 (정확 일치)
    Mono<Supplier> findBySupplierName(String supplierName);

    // 공급업체명으로 조회 (부분 일치)
    @Query("SELECT * FROM suppliers WHERE supplier_name ILIKE '%' || :supplierName || '%'")
    Flux<Supplier> findBySupplierNameContaining(String supplierName);

    // 활성화된 공급업체만 조회
    Flux<Supplier> findByIsActiveTrue();

    // 비활성화된 공급업체 조회
    Flux<Supplier> findByIsActiveFalse();

    // 이메일로 조회
    Mono<Supplier> findByEmail(String email);

    // 담당자명으로 조회
    Flux<Supplier> findByContactPerson(String contactPerson);

    // 전화번호로 조회
    Mono<Supplier> findByPhone(String phone);

    // 사업자등록번호로 조회
    Mono<Supplier> findByTaxId(String taxId);

    // 주소 검색 (부분 일치)
    @Query("SELECT * FROM suppliers WHERE address ILIKE '%' || :address || '%'")
    Flux<Supplier> findByAddressContaining(String address);

    // 최근 생성된 공급업체
    @Query("SELECT * FROM suppliers ORDER BY created_at DESC LIMIT :limit")
    Flux<Supplier> findRecentSuppliers(int limit);
}
