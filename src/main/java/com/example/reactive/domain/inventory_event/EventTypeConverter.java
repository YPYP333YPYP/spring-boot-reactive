package com.example.reactive.domain.inventory_event;

import com.example.reactive.domain.inventory_event.InventoryEvent.EventType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class EventTypeConverter {

    @ReadingConverter
    public static class StringToEventTypeConverter implements Converter<String, EventType> {
        @Override
        public EventType convert(String source) {
            return EventType.valueOf(source);
        }
    }

    @WritingConverter
    public static class EventTypeToStringConverter implements Converter<EventType, String> {
        @Override
        public String convert(EventType source) {
            return source.name();
        }
    }
}