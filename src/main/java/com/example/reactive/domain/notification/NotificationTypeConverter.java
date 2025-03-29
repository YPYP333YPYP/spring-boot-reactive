package com.example.reactive.domain.notification;

import com.example.reactive.domain.notification.Notification.NotificationType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class NotificationTypeConverter {

    @ReadingConverter
    public static class StringToNotificationTypeConverter implements
        Converter<String, NotificationType> {
        @Override
        public NotificationType convert(String source) {
            return NotificationType.valueOf(source);
        }
    }

    @WritingConverter
    public static class NotificationTypeToStringConverter implements Converter<NotificationType, String> {
        @Override
        public String convert(NotificationType source) {
            return source.name();
        }
    }
}
