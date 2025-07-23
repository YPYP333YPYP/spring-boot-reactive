package com.example.reactive.global.scheduler;

import com.example.reactive.domain.order.AutoOrderService;
import com.example.reactive.domain.order.PurchaseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryScheduler {

    private final AutoOrderService autoOrderService;

    /**
     * 매일 오전 9시에 재고 체크 및 자동 발주 실행
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void dailyInventoryCheck() {
        log.info("일일 재고 체크 스케줄 시작");

        autoOrderService.checkAndCreateAutoOrders()
            .doOnNext(order -> log.info("자동 발주 완료: {}", order.getId()))
            .doOnError(error -> log.error("자동 발주 중 오류 발생", error))
            .subscribe();
    }

    /**
     * 매일 오후 6시에 유통기한 체크
     */
    @Scheduled(cron = "0 0 18 * * *")
    public void dailyExpiryCheck() {
        log.info("일일 유통기한 체크 스케줄 시작");

        autoOrderService.checkExpiryAlerts()
            .doOnNext(notification -> log.info("유통기한 알림 발송: {}", notification.getId()))
            .doOnError(error -> log.error("유통기한 체크 중 오류 발생", error))
            .subscribe();
    }

    /**
     * 매 시간마다 긴급 재고 체크 (재고가 0에 가까운 경우)
     */
    @Scheduled(fixedRate = 3600000) // 1시간마다
    public void hourlyEmergencyCheck() {
        log.info("시간별 긴급 재고 체크 시작");

        // 긴급 재고 체크 로직 (재고가 임계값의 50% 이하)
        autoOrderService.checkAndCreateAutoOrders()
            .filter(order -> order.getOrderType() == PurchaseOrder.OrderType.EMERGENCY)
            .doOnNext(order -> log.warn("긴급 발주 생성: {}", order.getId()))
            .subscribe();
    }
}