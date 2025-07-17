package com.example.reactive.domain.order;



import com.example.reactive.domain.dto.DeliveryResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PurchaseOrderService orderService;

    /**
     * 수동 발주 생성
     */
    @PostMapping
    public Mono<ResponseEntity<PurchaseOrder>> createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createManualOrder(request)
            .map(ResponseEntity::ok);
    }

    /**
     * 발주 상태 변경
     */
    @PutMapping("/{orderId}/status")
    public Mono<ResponseEntity<PurchaseOrder>> updateOrderStatus(
        @PathVariable Long orderId,
        @RequestParam PurchaseOrder.OrderStatus status) {

        return orderService.updateOrderStatus(orderId, status)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 발주 목록 조회
     */
    @GetMapping
    public Flux<PurchaseOrder> getOrders(
        @RequestParam(required = false) PurchaseOrder.OrderStatus status,
        @RequestParam(required = false) Long supplierId) {

        if (status != null) {
            return orderService.getOrdersByStatus(status);
        } else if (supplierId != null) {
            return orderService.getOrdersBySupplier(supplierId);
        }
        return orderService.getAllOrders();
    }

    /**
     * 발주 상세 조회
     */
    @GetMapping("/{orderId}")
    public Mono<ResponseEntity<PurchaseOrder>> getOrder(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 납품 완료 처리 (재고 자동 증가) - 수정된 버전
     */
    @PostMapping("/{orderId}/complete-delivery")
    public Mono<ResponseEntity<DeliveryResponse>> completeDelivery(
        @PathVariable Long orderId,
        @RequestParam(required = false) Double actualQuantity) {

        return orderService.completeDelivery(orderId, actualQuantity)
            .map(result -> {
                DeliveryResponse response = new DeliveryResponse(
                    "납품 완료 처리됨",
                    result.getOrderId(),
                    result.getDeliveredQuantity()
                );
                return ResponseEntity.ok(response);
            })
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Request DTO
    @Data
    public static class CreateOrderRequest {
        private Long supplierId;
        private Long productId;
        private Long warehouseId;
        private Double requestedQuantity;
        private Double unitPrice;
        private String notes;
    }
}