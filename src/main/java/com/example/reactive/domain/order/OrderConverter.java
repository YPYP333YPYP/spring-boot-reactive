package com.example.reactive.domain.order;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class OrderConverter {

    @ReadingConverter
    public static class StringToOrderStatusConverter implements Converter<String, PurchaseOrder.OrderStatus> {
        @Override
        public PurchaseOrder.OrderStatus convert(String source) {
            return PurchaseOrder.OrderStatus.valueOf(source);
        }
    }

    @WritingConverter
    public static class OrderStatusToStringConverter implements Converter<PurchaseOrder.OrderStatus, String> {
        @Override
        public String convert(PurchaseOrder.OrderStatus source) {
            return source.name();
        }
    }

    @ReadingConverter
    public static class StringToOrderTypeConverter implements Converter<String, PurchaseOrder.OrderType> {
        @Override
        public PurchaseOrder.OrderType convert(String source) {
            return PurchaseOrder.OrderType.valueOf(source);
        }
    }

    @WritingConverter
    public static class OrderTypeToStringConverter implements Converter<PurchaseOrder.OrderType, String> {
        @Override
        public String convert(PurchaseOrder.OrderType source) {
            return source.name();
        }
    }
}