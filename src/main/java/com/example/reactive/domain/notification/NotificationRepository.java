package com.example.reactive.domain.notification;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {

    // 사용자별 알림 조회
    Flux<Notification> findByUserId(Long userId);

    // 읽지 않은 알림 조회 (사용자별)
    Flux<Notification> findByUserIdAndIsReadFalse(Long userId);

    // 모든 읽지 않은 알림 조회 (대시보드용) - 누락된 메서드 추가
    Flux<Notification> findByIsReadFalse();

    // 읽은 알림 조회
    Flux<Notification> findByIsReadTrue();

    // 알림 타입별 조회
    Flux<Notification> findByNotificationType(Notification.NotificationType notificationType);

    // 특정 타입의 읽지 않은 알림 조회
    Flux<Notification> findByNotificationTypeAndIsReadFalse(Notification.NotificationType notificationType);

    // 최근 알림 조회 (생성일 기준)
    @Query("SELECT * FROM notifications ORDER BY created_at DESC LIMIT :limit")
    Flux<Notification> findRecentNotifications(int limit);

    // 사용자별 최근 알림 조회
    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    Flux<Notification> findRecentNotificationsByUser(Long userId, int limit);

    // 오늘 생성된 알림 조회
    @Query("SELECT * FROM notifications WHERE DATE(created_at) = CURRENT_DATE ORDER BY created_at DESC")
    Flux<Notification> findTodayNotifications();

    // 사용자별 읽지 않은 알림 개수
    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = false")
    Mono<Long> countUnreadByUserId(Long userId);

    // 알림 타입별 개수
    @Query("SELECT type, COUNT(*) as count FROM notifications GROUP BY type")
    Flux<Object[]> countByNotificationType();

    // 특정 기간 내 알림 조회
    @Query("SELECT * FROM notifications WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    Flux<Notification> findByCreatedAtBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}