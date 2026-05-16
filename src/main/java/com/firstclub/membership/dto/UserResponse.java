package com.firstclub.membership.dto;

import com.firstclub.membership.entity.User;
import java.time.LocalDateTime;

public record UserResponse(Long id, String name, String email, String phone, String cohort, LocalDateTime createdAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getPhone(), user.getCohort(), user.getCreatedAt());
    }
}
