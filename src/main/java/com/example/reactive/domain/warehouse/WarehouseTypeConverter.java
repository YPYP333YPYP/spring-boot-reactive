package com.example.reactive.domain.warehouse;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import com.example.reactive.domain.warehouse.Warehouse.WarehouseType;

public class WarehouseTypeConverter {

    @ReadingConverter
    public static class StringToWarehouseTypeConverter implements Converter<String, WarehouseType> {
        @Override
        public WarehouseType convert(String source) {
            return WarehouseType.valueOf(source);
        }
    }

    @WritingConverter
    public static class WarehouseTypeToStringConverter implements Converter<WarehouseType, String> {
        @Override
        public String convert(WarehouseType source) {
            return source.name();
        }
    }
}
