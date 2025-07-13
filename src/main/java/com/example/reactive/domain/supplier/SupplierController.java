package com.example.reactive.domain.supplier;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * 공급업체 생성
     */
    @PostMapping
    public Mono<ResponseEntity<Supplier>> createSupplier(@RequestBody Supplier supplier) {
        return supplierService.createSupplier(supplier)
            .map(ResponseEntity::ok);
    }

    /**
     * 공급업체 수정
     */
    @PutMapping("/{supplierId}")
    public Mono<ResponseEntity<Supplier>> updateSupplier(
        @PathVariable Long supplierId,
        @RequestBody Supplier supplier) {

        return supplierService.updateSupplier(supplierId, supplier)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 공급업체 조회
     */
    @GetMapping("/{supplierId}")
    public Mono<ResponseEntity<Supplier>> getSupplier(@PathVariable Long supplierId) {
        return supplierService.getSupplierById(supplierId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 공급업체명으로 조회
     */
    @GetMapping("/name/{supplierName}")
    public Mono<ResponseEntity<Supplier>> getSupplierByName(@PathVariable String supplierName) {
        return supplierService.getSupplierByName(supplierName)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 모든 공급업체 조회
     */
    @GetMapping
    public Flux<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    /**
     * 활성 공급업체만 조회
     */
    @GetMapping("/active")
    public Flux<Supplier> getActiveSuppliers() {
        return supplierService.getActiveSuppliers();
    }

    /**
     * 공급업체 검색
     */
    @GetMapping("/search")
    public Flux<Supplier> searchSuppliers(@RequestParam String name) {
        return supplierService.searchSuppliers(name);
    }

    /**
     * 최근 공급업체 조회
     */
    @GetMapping("/recent")
    public Flux<Supplier> getRecentSuppliers(@RequestParam(defaultValue = "10") int limit) {
        return supplierService.getRecentSuppliers(limit);
    }

    /**
     * 공급업체 비활성화
     */
    @PutMapping("/{supplierId}/deactivate")
    public Mono<ResponseEntity<Supplier>> deactivateSupplier(@PathVariable Long supplierId) {
        return supplierService.deactivateSupplier(supplierId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 공급업체 활성화
     */
    @PutMapping("/{supplierId}/activate")
    public Mono<ResponseEntity<Supplier>> activateSupplier(@PathVariable Long supplierId) {
        return supplierService.activateSupplier(supplierId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 공급업체 삭제
     */
    @DeleteMapping("/{supplierId}")
    public Mono<ResponseEntity<Void>> deleteSupplier(@PathVariable Long supplierId) {
        return supplierService.deleteSupplier(supplierId)
            .then(Mono.just(ResponseEntity.ok().<Void>build()))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}