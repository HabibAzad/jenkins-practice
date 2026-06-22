package com.qacopilot.support;

import io.cucumber.java.Scenario;
import io.restassured.path.json.JsonPath;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runtime variable store that backs the flow-aware Gherkin steps emitted by
 * {@code GherkinFlowRenderer}. Captured values flow between steps in a scenario, between
 * scenarios in the same flow (when tagged {@code @flow_id_<id>}), and across the whole suite.
 *
 * <p>Three scopes are supported:
 * <ul>
 *   <li>{@link Scope#SCENARIO} — cleared by every {@code @Before} hook (default for body fields).</li>
 *   <li>{@link Scope#FLOW} — keyed by the {@code @flow_id_<id>} tag, shared across scenarios that
 *       carry the same flow id. Cleared when the flow id changes between scenarios.</li>
 *   <li>{@link Scope#SUITE} — process-wide static map; never cleared (typical for an auth bearer
 *       token captured once and reused everywhere).</li>
 * </ul>
 *
 * <p>Lookups use the precedence {@code SCENARIO &rarr; FLOW &rarr; SUITE} so a more specific scope
 * always shadows a broader one. {@link #substitute(String)} replaces {@code ${var}} placeholders
 * with the resolved value (matching the renderer's template syntax) and leaves unknown
 * references intact for visibility.</p>
 */
public final class ScenarioContextStore {

    /** Variable lifetime. Mirrors {@code com.qacopilot.generation.flow.BindingScope}. */
    public enum Scope {SCENARIO, FLOW, SUITE}

    private static final Pattern VARIABLE_REFERENCE = Pattern.compile("\\$\\{([a-zA-Z0-9_.]+)}");
    private static final Pattern FLOW_ID_TAG = Pattern.compile("^@flow_id_(.+)$");

    private static final Map<String, Map<String, String>> FLOW_VARS_BY_FLOW_ID = new ConcurrentHashMap<>();
    private static final Map<String, String> SUITE_VARS = new ConcurrentHashMap<>();

    private final Map<String, String> scenarioVars = new LinkedHashMap<>();
    private String currentFlowId;
    private String lastResponseBody;

    /**
     * Resets the SCENARIO scope, advances the flow id (clearing FLOW scope when the id changes),
     * and clears the cached previous-response body. SUITE scope is preserved.
     *
     * <p>Called from {@code @Before} so every Cucumber scenario starts with a clean local view of
     * the variable store while keeping any longer-lived bindings intact.</p>
     */
    public void resetForScenario(Scenario scenario) {
        scenarioVars.clear();
        lastResponseBody = null;
        String previousFlowId = currentFlowId;
        String detected = extractFlowId(scenario);
        currentFlowId = detected;
        // Crossing into a new flow: discard the previous flow's variables so a stale userId
        // from flow A cannot leak into flow B's first scenario. Guard the remove because
        // `previousFlowId` is null on the first scenario AND `ConcurrentHashMap` rejects
        // a null key with NullPointerException ("Cannot invoke Object.hashCode()").
        if (detected != null
                && previousFlowId != null
                && !Objects.equals(detected, previousFlowId)) {
            FLOW_VARS_BY_FLOW_ID.remove(previousFlowId);
        }
    }

    private static String extractFlowId(Scenario scenario) {
        if (scenario == null) {
            return null;
        }
        Collection<String> tags = scenario.getSourceTagNames();
        if (tags == null) {
            return null;
        }
        for (String tag : tags) {
            Matcher m = FLOW_ID_TAG.matcher(tag);
            if (m.matches()) {
                return m.group(1);
            }
        }
        return null;
    }

    /** Records {@code value} under {@code name} in the requested scope. */
    public void put(String name, String value, Scope scope) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(scope, "scope");
        String resolved = value == null ? "" : value;
        switch (scope) {
            case SCENARIO -> scenarioVars.put(name, resolved);
            case FLOW -> flowVars().put(name, resolved);
            case SUITE -> SUITE_VARS.put(name, resolved);
        }
    }

    /** Looks up {@code name} with SCENARIO &rarr; FLOW &rarr; SUITE precedence. */
    public Optional<String> get(String name) {
        if (name == null) {
            return Optional.empty();
        }
        if (scenarioVars.containsKey(name)) {
            return Optional.of(scenarioVars.get(name));
        }
        Map<String, String> flow = flowVarsOrNull();
        if (flow != null && flow.containsKey(name)) {
            return Optional.of(flow.get(name));
        }
        if (SUITE_VARS.containsKey(name)) {
            return Optional.of(SUITE_VARS.get(name));
        }
        return Optional.empty();
    }

    /**
     * Replaces every {@code ${var}} placeholder in {@code template} with its resolved value.
     * Unresolved references are left intact so failures surface in logs / HTML report rather
     * than producing silently-empty URLs.
     */
    public String substitute(String template) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        Matcher m = VARIABLE_REFERENCE.matcher(template);
        StringBuilder out = new StringBuilder(template.length());
        while (m.find()) {
            String var = m.group(1);
            String replacement = get(var).orElse(m.group(0));
            m.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(out);
        return out.toString();
    }

    /** Records the body of the most recent HTTP response so {@link #captureFromLastResponse} can read it. */
    public void rememberResponseBody(String body) {
        this.lastResponseBody = body == null ? "" : body;
    }

    public String getLastResponseBody() {
        return lastResponseBody == null ? "" : lastResponseBody;
    }

    /**
     * Extracts a value from the previous response using a JsonPath expression (e.g. {@code $.id},
     * {@code $.data.user.id}, {@code accessToken}). Returns {@link Optional#empty()} when the
     * body is missing or not parseable as JSON; callers decide whether to treat that as fatal.
     */
    public Optional<String> captureFromLastResponse(String jsonPathExpr, String variable, Scope scope) {
        Objects.requireNonNull(variable, "variable");
        Objects.requireNonNull(scope, "scope");
        String body = getLastResponseBody();
        if (body.isBlank()) {
            return Optional.empty();
        }
        String gpath = toGPath(jsonPathExpr);
        try {
            Object value = gpath.isEmpty()
                    ? JsonPath.from(body).get()
                    : JsonPath.from(body).get(gpath);
            String stringified = value == null ? "" : value.toString();
            put(variable, stringified, scope);
            return Optional.of(stringified);
        } catch (RuntimeException ex) {
            // Body wasn't JSON, or the path didn't resolve to a leaf — leave the var unset.
            return Optional.empty();
        }
    }

    /**
     * Records a deterministic stub value during dry-run mode so subsequent {@code ${var}}
     * substitutions still produce non-empty URLs and headers in the HTML report.
     */
    public String captureDryRunStub(String variable, Scope scope) {
        String stub = "dry-run-" + variable;
        put(variable, stub, scope);
        return stub;
    }

    /**
     * Converts a JsonPath expression to Rest Assured's GPath syntax.
     *
     * <p>The renderer emits canonical JsonPath strings like {@code $.id} or {@code $.data.user.id}.
     * Rest Assured's {@link io.restassured.path.json.JsonPath} consumes GPath, where the leading
     * {@code $} root is implicit. The conversion strips {@code $} / {@code $.} and accepts
     * pre-stripped expressions verbatim.</p>
     */
    static String toGPath(String jsonPath) {
        if (jsonPath == null) {
            return "";
        }
        String trimmed = jsonPath.trim();
        if (trimmed.isEmpty() || trimmed.equals("$")) {
            return "";
        }
        if (trimmed.startsWith("$.")) {
            return trimmed.substring(2);
        }
        if (trimmed.startsWith("$[")) {
            return trimmed.substring(1);
        }
        return trimmed;
    }

    /** Strips the surrounding {@code ${...}} from a renderer-emitted variable reference. */
    public static String unwrapVariableReference(String raw) {
        if (raw == null) {
            return null;
        }
        String t = raw.trim();
        if (t.startsWith("${") && t.endsWith("}") && t.length() >= 3) {
            return t.substring(2, t.length() - 1);
        }
        return t;
    }

    private Map<String, String> flowVars() {
        if (currentFlowId == null) {
            // No flow tag on the scenario — a FLOW write degrades to SCENARIO so the data is at
            // least visible to subsequent steps in this scenario.
            return scenarioVars;
        }
        return FLOW_VARS_BY_FLOW_ID.computeIfAbsent(currentFlowId, k -> new ConcurrentHashMap<>());
    }

    private Map<String, String> flowVarsOrNull() {
        if (currentFlowId == null) {
            return null;
        }
        return FLOW_VARS_BY_FLOW_ID.get(currentFlowId);
    }

    /** Test/diagnostic accessor — exposes the current SCENARIO-scope view. */
    public Map<String, String> snapshotScenarioVars() {
        return new LinkedHashMap<>(scenarioVars);
    }

    /** Test/diagnostic accessor — exposes the current SUITE-scope view. */
    public static Map<String, String> snapshotSuiteVars() {
        return new LinkedHashMap<>(SUITE_VARS);
    }

    /** Test hook — clears every static scope. Production code never calls this. */
    static void clearAllStaticScopes() {
        FLOW_VARS_BY_FLOW_ID.clear();
        SUITE_VARS.clear();
    }
}
