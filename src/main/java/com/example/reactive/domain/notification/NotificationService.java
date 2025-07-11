package com.example.reactive.domain.notification;

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
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림 생성
     */
    @Transactional
    public Mono<Notification> createNotification(
        Long userId,
        String title,
        String message,
        Notification.NotificationType type) {

        Notification notification = Notification.builder()
            .userId(userId)
            .title(title)
            .message(message)
            .notificationType(type)
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();

        return notificationRepository.save(notification)
            .doOnNext(savedNotification -> log.info("알림 생성: 사용자={}, 타입={}",
                userId, type));
    }

    /**
     * 사용자별 알림 조회
     */
    public Flux<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    /**
     * 읽지 않은 알림 조회
     */
    public Flux<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public Mono<Notification> markAsRead(Long notificationId) {
        return notificationRepository.findById(notificationId)
            .flatMap(notification -> {
                notification.setIsRead(true);
                return notificationRepository.save(notification);
            })
            .doOnNext(notification -> log.info("알림 읽음 처리: ID={}", notificationId));
    }

    /**
     * 모든 알림 읽음 처리
     */
    @Transactional
    public Flux<Notification> markAllAsRead(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId)
            .flatMap(notification -> {
                notification.setIsRead(true);
                return notificationRepository.save(notification);
            })
            .doOnNext(notification -> log.debug("알림 읽음 처리: ID={}", notification.getId()));
    }

    /**
     * 알림 삭제
     */
    @Transactional
    public Mono<Void> deleteNotification(Long notificationId) {
        return notificationRepository.deleteById(notificationId)
            .doOnSuccess(unused -> log.info("알림 삭제: ID={}", notificationId));
    }

    /**
     * 알림 타입별 조회
     */
    public Flux<Notification> getNotificationsByType(Notification.NotificationType type) {
        return notificationRepository.findByNotificationType(type);
    }

    /**
     * 임계값 알림 생성 (편의 메서드)
     */
    public Mono<Notification> createThresholdAlert(
        Long userId,
        String productName,
        Double currentQuantity,
        String unit,
        Long autoOrderId) {

        String title = "재고 부족 알림";
        String message = String.format(
            "상품 '%s'의 재고가 부족합니다.\n" +
                "현재 재고: %.2f %s\n" +
                "자동 발주가 생성되었습니다. (발주번호: %d)",
            productName, currentQuantity, unit, autoOrderId
        );

        return createNotification(userId, title, message, Notification.NotificationType.THRESHOLD_ALERT);
    }

    /**
     * 유통기한 알림 생성 (편의 메서드)
     */
    public Mono<Notification> createExpiryAlert(
        Long userId,
        String productName,
        LocalDateTime expiryDate,
        String location,
        Double quantity,
        String unit) {

        String title = "유통기한 임박 알림";
        String message = String.format(
            "상품 '%s'의 유통기한이 임박했습니다.\n" +
                "유통기한: %s\n" +
                "현재 재고: %.2f %s\n" +
                "위치: %s",
            productName, expiryDate.toLocalDate(), quantity, unit, location
        );

        return createNotification(userId, title, message, Notification.NotificationType.EXPIRY_ALERT);
    }

    /**
     * 시스템 알림 생성 (편의 메서드)
     */
    public Mono<Notification> createSystemNotification(
        Long userId,
        String title,
        String message) {

        return createNotification(userId, title, message, Notification.NotificationType.SYSTEM_NOTIFICATION);
    }
}