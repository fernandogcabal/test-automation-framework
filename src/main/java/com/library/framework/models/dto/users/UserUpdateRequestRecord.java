package com.library.framework.models.dto.users;

public record UserUpdateRequestRecord(
        String username,
        String email,
        String password
) {
}
