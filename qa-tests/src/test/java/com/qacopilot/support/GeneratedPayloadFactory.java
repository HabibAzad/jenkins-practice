package com.qacopilot.support;

public final class GeneratedPayloadFactory {
    private GeneratedPayloadFactory() {
    }

    public static String payloadFor(String requestType) {
        if (requestType == null || requestType.isBlank()) {
            return "{}";
        }
        return switch (requestType) {
            default -> "{}";
        };
    }
}
