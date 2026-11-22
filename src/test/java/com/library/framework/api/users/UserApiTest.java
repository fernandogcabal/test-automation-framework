package com.library.framework.api.users;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.library.framework.api.AuthenticatedApiTest;
import com.library.framework.config.ConfigManager;
import com.library.framework.models.dto.auth.AuthRequestRecord;
import com.library.framework.models.dto.users.UserRegisterRequestRecord;
import com.library.framework.models.dto.users.UserUpdateRequestRecord;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class UserApiTest extends AuthenticatedApiTest {

    @Test(groups = "api")
    public void shouldRejectRegistrationWithEmptyUsername() {
        UserRegisterRequestRecord invalidPayload =
                new UserRegisterRequestRecord(
                        "",
                        "invalid-registration@example.test",
                        "TestPassword123!");

        Response response = client.post("/api/users", invalidPayload);
        assertTrue(
                response.statusCode() >= 400,
                "Expected invalid registration to be rejected, but got HTTP "
                        + response.statusCode() + ": " + response.asString());
    }

    @Test(groups = "api")
    public void shouldLoginUser() {
        String username = ConfigManager.getRequired("api.username");
        String password = ConfigManager.getRequired("api.password");
        Response loginResponse =
                client.post("/api/users/login", new AuthRequestRecord(username, password));

        assertEquals(
                loginResponse.statusCode(),
                200,
                "Logging in with the configured account failed: " + loginResponse.asString());
        String token = loginResponse.jsonPath().getString("token");
        assertNotNull(token, "Login response did not contain an authentication token");
        assertFalse(token.isBlank(), "Login returned an empty authentication token");
    }

    @Test(groups = "api")
    public void shouldGetCurrentUser() {
        Response userResponse = client.get("/api/users/me");
        assertEquals(
                userResponse.statusCode(),
                200,
                "Fetching the authenticated user failed: " + userResponse.asString());
        assertEquals(
                userResponse.jsonPath().getString("username"),
                ConfigManager.getRequired("api.username"),
                "Current-user endpoint returned an unexpected username");
    }

    @Test(groups = "api")
    public void shouldUpdateUser() {
        Response currentUserResponse = client.get("/api/users/me");
        assertEquals(
                currentUserResponse.statusCode(),
                200,
                "Fetching profile before update failed: " + currentUserResponse.asString());
        String originalUsername = currentUserResponse.jsonPath().getString("username");
        String originalEmail = currentUserResponse.jsonPath().getString("email");
        assertNotNull(originalUsername, "Current-user response did not contain a username");
        assertNotNull(originalEmail, "Current-user response did not contain an email");

        String password = ConfigManager.getRequired("api.password");
        String updatedEmail = "api-test+" + System.nanoTime() + "@example.test";
        try {
            UserUpdateRequestRecord updatePayload =
                    new UserUpdateRequestRecord(originalUsername, updatedEmail, password);
            Response updateResponse = client.put("/api/users/me", updatePayload);
            assertEquals(
                    updateResponse.statusCode(),
                    200,
                    "Updating the user profile failed: " + updateResponse.asString());
            assertEquals(updateResponse.jsonPath().getString("username"), originalUsername);
            assertEquals(updateResponse.jsonPath().getString("email"), updatedEmail);
        } finally {
            UserUpdateRequestRecord restorePayload =
                    new UserUpdateRequestRecord(originalUsername, originalEmail, password);
            Response restoreResponse = client.put("/api/users/me", restorePayload);
            assertEquals(
                    restoreResponse.statusCode(),
                    200,
                    "Restoring the original user profile failed: " + restoreResponse.asString());
        }
    }
}
