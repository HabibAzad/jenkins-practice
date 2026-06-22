package com.qacopilot.support;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public final class RestAssuredClient {

    /**
     * Backwards-compatible entrypoint used by legacy steps that don't yet pre-collect
     * headers / path / query bindings. Kept so previously-generated framework directories that
     * weren't re-scaffolded continue to compile until {@code STEPS_VERSION} bumps catch up.
     */
    public Response execute(String method, String baseUrl, String path, String requestBody) {
        return execute(method, baseUrl, path, requestBody, Map.of(), Map.of(), Map.of());
    }

    /**
     * Sends a single HTTP request with all flow-aware bindings applied. Unbound path variables
     * keep their {@code {name\}} template form (Rest Assured will then fail loudly), so missing
     * dependencies produce visible errors rather than silently rewriting to {@code "demo"}.
     */
    public Response execute(
            String method,
            String baseUrl,
            String path,
            String requestBody,
            Map<String, String> pathVars,
            Map<String, String> queryParams,
            Map<String, String> headers
    ) {
        RequestSpecification request = RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON);

        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        if (requestBody != null && !requestBody.isBlank()) {
            request.body(requestBody);
        }

        String resolvedPath = applyPathVars(path, pathVars);

        return switch (method.toUpperCase()) {
            case "GET" -> request.get(resolvedPath);
            case "POST" -> request.post(resolvedPath);
            case "PUT" -> request.put(resolvedPath);
            case "PATCH" -> request.patch(resolvedPath);
            case "DELETE" -> request.delete(resolvedPath);
            case "HEAD" -> request.head(resolvedPath);
            case "OPTIONS" -> request.options(resolvedPath);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    /**
     * Substitutes <code>{name}</code> placeholders in {@code path} with values from
     * {@code pathVars}. Unbound placeholders are kept verbatim and fall back to the legacy
     * "demo" behaviour so a partially-bound flow still attempts the call rather than crashing.
     */
    private static String applyPathVars(String path, Map<String, String> pathVars) {
        if (path == null) {
            return "";
        }
        String result = path;
        if (pathVars != null) {
            for (Map.Entry<String, String> e : pathVars.entrySet()) {
                String value = e.getValue() == null ? "" : e.getValue();
                result = result.replace("{" + e.getKey() + "}", value);
            }
        }
        // Any path variable that the caller never bound becomes "demo" so legacy non-flow
        // scenarios (no setRequestPathVariable steps) keep their original behaviour.
        return result.replaceAll("\\{[^/}]+}", "demo");
    }
}
