package com.library.framework.api;

import com.library.framework.config.ConfigManager;
import com.library.framework.models.dto.auth.AuthRequestRecord;
import com.library.framework.models.dto.auth.AuthResponseRecord;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;

public abstract class AuthenticatedApiTest {
    protected ApiClient client;

    @BeforeClass(alwaysRun = true)
    public void authenticate() {
        client = new ApiClient(ConfigManager.getRequired("api.base.url"));

        AuthRequestRecord credentials = new AuthRequestRecord(
                ConfigManager.getRequired("api.username"),
                ConfigManager.getRequired("api.password"));

        Response response = client.post("/api/users/login", credentials);
        if (response.statusCode() != 200) {
            throw new AssertionError(
                    "Login failed with HTTP " + response.statusCode()
                            + ": " + response.asString());
        }

        AuthResponseRecord auth = response.as(AuthResponseRecord.class);
        client.setToken(auth.token());
    }
}
