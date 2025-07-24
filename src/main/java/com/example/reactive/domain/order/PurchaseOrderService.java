package com.example.reactive.domain.order;

import com.example.reactive.domain.inventory.InventoryService;
import com.example.reactive.domain.order.OrderController.CreateOrderRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepository;
    private final InventoryService inventoryService;

    /**
     * 수동 발주 생성
     */
    @Transactional
    public Mono<PurchaseOrder> createManualOrder(CreateOrderRequest request) {
        PurchaseOrder order = PurchaseOrder.builder()
            .supplierId(request.getSupplierId())
            .productId(request.getProductId())
            .warehouseId(request.getWarehouseId())
            .requestedBy(1L) // 현재 사용자 ID (실제로는 SecurityContext에서 가져와야 함)
            .requestedQuantity(request.getRequestedQuantity())
            .unitPrice(request.getUnitPrice())
            .totalAmount(request.getRequestedQuantity() * request.getUnitPrice())
            .status(PurchaseOrder.OrderStatus.PENDING)
            .orderType(PurchaseOrder.OrderType.MANUAL)
            .notes(request.getNotes())
            .requestedAt(LocalDateTime.now())
            .expectedDeliveryDate(LocalDateTime.now().plusDays(3))
            .createdAt(LocalDateTime.now())
            .build();

        return orderRepository.save(order)
            .doOnNext(savedOrder -> log.info("수동 발주 생성: {}", savedOrder.getId()));
    }

    /**
     * 발주 상태 업데이트
     */
    @Transactional
    public Mono<PurchaseOrder> updateOrderStatus(Long orderId, PurchaseOrder.OrderStatus newStatus) {
        return orderRepository.findById(orderId)
            .flatMap(order -> {
                order.setStatus(newStatus);
                order.setUpdatedAt(LocalDateTime.now());

                // 납품 완료 시 실제 납품일 설정
                if (newStatus == PurchaseOrder.OrderStatus.DELIVERED) {
                    order.setActualDeliveryDate(LocalDateTime.now());
                }

                return orderRepository.save(order);
            });
    }

    /**
     * 납품 완료 처리 (재고 자동 증가) - 수정된 버전
     */
    @Transactional
    public Mono<DeliveryResult> completeDelivery(Long orderId, Double actualQuantity) {
        return orderRepository.findById(orderId)
            .flatMap(order -> {
                Double deliveredQuantity = actualQuantity != null ? actualQuantity : order.getRequestedQuantity();

                // 발주 상태를 납품 완료로 변경
                return updateOrderStatus(orderId, PurchaseOrder.OrderStatus.DELIVERED)
                    .flatMap(updatedOrder -> {
                        // 해당 창고의 재고 찾기 또는 생성
                        return inventoryService.findOrCreateInventory(
                            order.getWarehouseId(),
                            order.getProductId()
                        ).flatMap(inventory -> {
                            // 재고 증가
                            return inventoryService.addStock(
                                inventory.getId(),
                                deliveredQuantity,
                                "발주 납품 완료 (발주번호: " + orderId + ")"
                            );
                        }).map(inventory -> new DeliveryResult(orderId, deliveredQuantity));
                    });
            })
            .doOnNext(result -> log.info("납품 완료 처리: 발주ID={}, 납품수량={}",
                result.getOrderId(), result.getDeliveredQuantity()));
    }

    public Flux<PurchaseOrder> getOrdersByStatus(PurchaseOrder.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public Flux<PurchaseOrder> getOrdersBySupplier(Long supplierId) {
        return orderRepository.findBySupplierId(supplierId);
    }

    public Flux<PurchaseOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<PurchaseOrder> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    // 결과 클래스
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeliveryResult {
        private Long orderId;
        private Double deliveredQuantity;
    }
}