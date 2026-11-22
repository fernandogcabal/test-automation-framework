package com.library.framework.models.dto.auth;
/*
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
 */

public record AuthResponseRecord(
        String token
) {
}
