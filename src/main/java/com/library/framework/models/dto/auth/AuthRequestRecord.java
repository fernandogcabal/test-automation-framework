package com.library.framework.models.dto.auth;
/*
• Private, final fields for each component (username and age).
• A canonical constructor initializing all fields.
• Public accessor methods matching the field names (e.g., user.username() and user.age(), rather than getUsername()).
• Fully functional toString(), equals(), and hashCode() implementations
 */

/*
{
  "username": "admin",
  "password": "Admin123"
}
 */

public record AuthRequestRecord(
        String username,
        String password
) {
}
