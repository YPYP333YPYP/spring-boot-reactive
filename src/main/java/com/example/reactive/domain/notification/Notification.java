package com.example.reactive.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("notifications")
public class Notification {
    @Id
    private Long id;

    @Column("user_id")
    private Long userId;
    private String title;
    private String message;
    private NotificationType notificationType;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public enum NotificationType {
        THRESHOLD_ALERT,
        EXPIRY_ALERT,
        SYSTEM_NOTIFICATION,
        TASK_ASSIGNMENT
    }
}
