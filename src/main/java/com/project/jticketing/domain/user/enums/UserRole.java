package com.project.jticketing.domain.user.enums;

import java.security.InvalidParameterException;
import java.util.Arrays;

public enum UserRole {
    ADMIN, OWNER, USER;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidParameterException("유효하지 않은 UerRole"));
    }
}