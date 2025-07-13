package com.example.reactive.domain.supplier;

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
public class SupplierService {

    private final SupplierRepository supplierRepository;

    /**
     * 공급업체 생성
     */
    @Transactional
    public Mono<Supplier> createSupplier(Supplier supplier) {
        supplier.setCreatedAt(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());
        supplier.setIsActive(true);

        return supplierRepository.save(supplier)
            .doOnNext(savedSupplier -> log.info("공급업체 생성: ID={}, 이름={}",
                savedSupplier.getId(), savedSupplier.getSupplierName()));
    }

    /**
     * 공급업체 수정
     */
    @Transactional
    public Mono<Supplier> updateSupplier(Long supplierId, Supplier updatedSupplier) {
        return supplierRepository.findById(supplierId)
            .flatMap(existingSupplier -> {
                existingSupplier.setSupplierName(updatedSupplier.getSupplierName());
                existingSupplier.setContactPerson(updatedSupplier.getContactPerson());
                existingSupplier.setEmail(updatedSupplier.getEmail());
                existingSupplier.setPhone(updatedSupplier.getPhone());
                existingSupplier.setAddress(updatedSupplier.getAddress());
                existingSupplier.setTaxId(updatedSupplier.getTaxId());
                existingSupplier.setNotes(updatedSupplier.getNotes());
                existingSupplier.setUpdatedAt(LocalDateTime.now());

                return supplierRepository.save(existingSupplier);
            })
            .doOnNext(savedSupplier -> log.info("공급업체 수정: ID={}", savedSupplier.getId()));
    }

    /**
     * 공급업체 조회
     */
    public Mono<Supplier> getSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId);
    }

    /**
     * 공급업체명으로 조회
     */
    public Mono<Supplier> getSupplierByName(String supplierName) {
        return supplierRepository.findBySupplierName(supplierName);
    }

    /**
     * 모든 공급업체 조회
     */
    public Flux<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    /**
     * 활성 공급업체만 조회
     */
    public Flux<Supplier> getActiveSuppliers() {
        return supplierRepository.findByIsActiveTrue();
    }

    /**
     * 공급업체 검색
     */
    public Flux<Supplier> searchSuppliers(String supplierName) {
        return supplierRepository.findBySupplierNameContaining(supplierName);
    }

    /**
     * 공급업체 비활성화
     */
    @Transactional
    public Mono<Supplier> deactivateSupplier(Long supplierId) {
        return supplierRepository.findById(supplierId)
            .flatMap(supplier -> {
                supplier.setIsActive(false);
                supplier.setUpdatedAt(LocalDateTime.now());
                return supplierRepository.save(supplier);
            })
            .doOnNext(supplier -> log.info("공급업체 비활성화: ID={}", supplier.getId()));
    }

    /**
     * 공급업체 활성화
     */
    @Transactional
    public Mono<Supplier> activateSupplier(Long supplierId) {
        return supplierRepository.findById(supplierId)
            .flatMap(supplier -> {
                supplier.setIsActive(true);
                supplier.setUpdatedAt(LocalDateTime.now());
                return supplierRepository.save(supplier);
            })
            .doOnNext(supplier -> log.info("공급업체 활성화: ID={}", supplier.getId()));
    }

    /**
     * 최근 공급업체 조회
     */
    public Flux<Supplier> getRecentSuppliers(int limit) {
        return supplierRepository.findRecentSuppliers(limit);
    }

    /**
     * 공급업체 삭제
     */
    @Transactional
    public Mono<Void> deleteSupplier(Long supplierId) {
        return supplierRepository.deleteById(supplierId)
            .doOnSuccess(unused -> log.info("공급업체 삭제: ID={}", supplierId));
    }
}