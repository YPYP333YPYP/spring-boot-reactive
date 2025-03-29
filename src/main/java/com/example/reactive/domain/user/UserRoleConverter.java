package com.example.reactive.domain.user;

import com.example.reactive.domain.user.User.UserRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class UserRoleConverter {

    @ReadingConverter
    public static class StringToUserRoleConverter implements Converter<String, UserRole> {
        @Override
        public UserRole convert(String source) {
            return UserRole.valueOf(source);
        }
    }

    @WritingConverter
    public static class UserRoleToStringConverter implements Converter<UserRole, String> {
        @Override
        public String convert(UserRole source) {
            return source.name();
        }
    }
}
