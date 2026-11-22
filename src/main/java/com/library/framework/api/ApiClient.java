package com.library.framework.api;

import java.util.concurrent.atomic.AtomicReference;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ApiClient {
    // Stores the current JWT in a thread-safe reference so it can be updated
    // and read by requests running in different threads.
    private final AtomicReference<String> token = new AtomicReference<>();

    // Shared request configuration for every HTTP call made by this client.
    private final RequestSpecification baseSpec;

    // A Rest Assured filter runs before each request. It reads the latest token
    // and adds the Bearer Authorization header when a token is available.
    private final Filter jwtFilter = (request, response, context) -> {
        String currentToken = token.get();
        if (currentToken != null && !currentToken.isBlank()) {
            request.header("Authorization", "Bearer " + currentToken);
        }

        // Continue with the request after applying the optional authentication header.
        return context.next(request, response);
    };

    /**
     * Creates an API client configured for the supplied base URL.
     * Requests use JSON content and automatically pass through the JWT filter.
     */
    public ApiClient(String baseUri) {
        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addFilter(jwtFilter)
                .build();
    }

    // Updates the token used by subsequent requests.
    public void setToken(String token) {
        this.token.set(token);
    }

    // Removes authentication from subsequent requests.
    public void clearToken() {
        token.set(null);
    }

    // Sends a GET request to the supplied path.
    public Response get(String path) {
        return given().spec(baseSpec).get(path);
    }

    // Sends a POST request with a serialized JSON request body.
    public Response post(String path, Object body) {
        return given().spec(baseSpec).body(body).post(path);
    }

    // Sends a PUT request with a serialized JSON request body.
    public Response put(String path, Object body) {
        return given().spec(baseSpec).body(body).put(path);
    }

    // Sends a DELETE request to the supplied path.
    public Response delete(String path) {
        return given().spec(baseSpec).delete(path);
    }
}
