package com.example.reactive.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 사용자별 알림 조회
     */
    @GetMapping("/user/{userId}")
    public Flux<Notification> getNotificationsByUser(@PathVariable Long userId) {
        return notificationService.getNotificationsByUser(userId);
    }

    /**
     * 읽지 않은 알림 조회
     */
    @GetMapping("/user/{userId}/unread")
    public Flux<Notification> getUnreadNotifications(@PathVariable Long userId) {
        return notificationService.getUnreadNotifications(userId);
    }

    /**
     * 알림 읽음 처리
     */
    @PutMapping("/{notificationId}/read")
    public Mono<ResponseEntity<Notification>> markAsRead(@PathVariable Long notificationId) {
        return notificationService.markAsRead(notificationId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 모든 알림 읽음 처리
     */
    @PutMapping("/user/{userId}/read-all")
    public Mono<ResponseEntity<String>> markAllAsRead(@PathVariable Long userId) {
        return notificationService.markAllAsRead(userId)
            .then(Mono.just(ResponseEntity.ok("모든 알림이 읽음 처리되었습니다.")));
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/{notificationId}")
    public Mono<ResponseEntity<Void>> deleteNotification(@PathVariable Long notificationId) {
        return notificationService.deleteNotification(notificationId)
            .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    /**
     * 알림 타입별 조회
     */
    @GetMapping("/type/{notificationType}")
    public Flux<Notification> getNotificationsByType(
        @PathVariable Notification.NotificationType notificationType) {

        return notificationService.getNotificationsByType(notificationType);
    }
}