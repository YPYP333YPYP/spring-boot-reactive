package com.example.reactive.domain.order;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PurchaseOrderRepository extends ReactiveCrudRepository<PurchaseOrder, Long> {

    // 공급업체별 발주 조회
    Flux<PurchaseOrder> findBySupplierId(Long supplierId);

    // 상품별 발주 조회
    Flux<PurchaseOrder> findByProductId(Long productId);

    // 상태별 발주 조회
    Flux<PurchaseOrder> findByStatus(PurchaseOrder.OrderStatus status);

    // 자동 발주 조회
    Flux<PurchaseOrder> findByOrderType(PurchaseOrder.OrderType orderType);

    // 특정 상품의 진행 중인 발주가 있는지 확인
    Mono<Boolean> existsByProductIdAndStatusIn(Long productId, java.util.List<PurchaseOrder.OrderStatus> statuses);
}
