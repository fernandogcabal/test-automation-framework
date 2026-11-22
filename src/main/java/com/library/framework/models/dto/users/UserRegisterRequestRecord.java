package com.library.framework.models.dto.users;

public record UserRegisterRequestRecord(
        String username,
        String email,
        String password
) {
}
