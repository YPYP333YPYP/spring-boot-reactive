package com.example.reactive.global.config;

import com.example.reactive.domain.inventory_event.EventTypeConverter;
import com.example.reactive.domain.notification.NotificationTypeConverter;
import com.example.reactive.domain.user.UserRoleConverter;
import com.example.reactive.domain.warehouse.WarehouseTypeConverter;
import io.r2dbc.spi.ConnectionFactory;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public ConnectionFactory connectionFactory() {
        return applicationContext.getBean(ConnectionFactory.class);
    }

    /*
    * ReadingConverter: 데이터베이스에서 읽은 String 값을 Java Enum 타입으로 변환
    * WritingConverter: Java Enum 타입을 데이터베이스에 저장할 String 값으로 변환
    * R2dbcCustomConversions: R2DBC가 사용할 커스텀 컨버터들을 등록하는 역할
    * */
    @Bean
    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Object> converters = new ArrayList<>();
        // Warehouse - WarehouseType Converter
        converters.add(new WarehouseTypeConverter.StringToWarehouseTypeConverter());
        converters.add(new WarehouseTypeConverter.WarehouseTypeToStringConverter());
        // InventoryEvent - EventType Converter
        converters.add(new EventTypeConverter.StringToEventTypeConverter());
        converters.add(new EventTypeConverter.EventTypeToStringConverter());
        // User - UserRole Converter
        converters.add(new UserRoleConverter.StringToUserRoleConverter());
        converters.add(new UserRoleConverter.UserRoleToStringConverter());
        // Notification - NotificationType Converter
        converters.add(new NotificationTypeConverter.StringToNotificationTypeConverter());
        converters.add(new NotificationTypeConverter.NotificationTypeToStringConverter());
        return super.r2dbcCustomConversions();
    }

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        initializer.setDatabasePopulator(populator);

        return initializer;
    }
}
