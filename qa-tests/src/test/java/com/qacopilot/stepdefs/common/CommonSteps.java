package com.qacopilot.stepdefs.common;

import com.qacopilot.support.ApiTestContext;
import com.qacopilot.support.GeneratedPayloadFactory;
import com.qacopilot.support.QaCopilotHtmlReport;
import com.qacopilot.support.RestAssuredClient;
import com.qacopilot.support.ScenarioContextStore;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;

import java.nio.charset.StandardCharsets;

/**
 * Shared REST/Gherkin step definitions used by all domain features. Keep a single definition per
 * step phrase here to avoid Cucumber duplicate-step errors. The flow-aware steps (header / path /
 * query bindings, JSON-path captures, JSON-path assertions) are emitted by
 * {@code GherkinFlowRenderer} and consume the {@link ScenarioContextStore} via
 * {@link ApiTestContext#getStore()}.
 */
public final class CommonSteps {
    private final ApiTestContext context = new ApiTestContext();
    private final RestAssuredClient client = new RestAssuredClient();
    private Scenario scenario;

    @Before
    public void bindScenario(Scenario scenario) {
        this.scenario = scenario;
        context.resetForScenario();
        context.getStore().resetForScenario(scenario);
    }

    @After
    public void emitHtmlReportRow(Scenario scenario) {
        QaCopilotHtmlReport.record(scenario, context);
    }

    private boolean dryRun() {
        return Boolean.parseBoolean(System.getProperty("qacopilot.dryRun", "false"));
    }

    @Given("API base URL is configured")
    public void apiBaseUrlIsConfigured() {
        String baseUrl = System.getProperty("api.base.url", "http://localhost:8080");
        context.setBaseUrl(baseUrl);
    }

    /**
     * No-op confirmation step paired with {@code Given API base URL is configured} by the flow
     * renderer. The actual reset happens in {@link #bindScenario(Scenario)}; this exists so
     * flow-rendered scenarios read top-to-bottom without surprising step gaps and so manual edits
     * can re-trigger a scenario-scope reset mid-test if needed.
     */
    @Given("a fresh test context")
    public void freshTestContext() {
        // Intentionally empty: @Before already cleared SCENARIO scope and pending bindings.
    }

    @And("set request header {string} to {string}")
    public void setRequestHeader(String name, String valueTemplate) {
        String resolved = context.getStore().substitute(valueTemplate);
        context.putPendingHeader(name, resolved);
    }

    @And("set request path variable {string} to {string}")
    public void setRequestPathVariable(String name, String valueTemplate) {
        String resolved = context.getStore().substitute(valueTemplate);
        context.putPendingPathVar(name, resolved);
    }

    @And("set request query parameter {string} to {string}")
    public void setRequestQueryParameter(String name, String valueTemplate) {
        String resolved = context.getStore().substitute(valueTemplate);
        context.putPendingQueryParam(name, resolved);
    }

    @And("request body matches {word}")
    public void requestBodyMatches(String requestType) {
        context.setRequestBody(GeneratedPayloadFactory.payloadFor(requestType));
    }

    @And("request body is:")
    public void requestBodyIsDocString(String body) {
        String raw = body == null ? "" : body.strip();
        context.setRequestBody(context.getStore().substitute(raw));
    }

    @When("I send a {word} request to {string}")
    public void iSendARequestTo(String method, String pathTemplate) {
        String resolvedPath = context.getStore().substitute(pathTemplate);
        context.setLastHttpMethod(method);
        context.setLastRequestPath(resolvedPath);
        context.setLastRequestBodySnapshot(context.getRequestBody());
        context.setLastFullUrl(QaCopilotHtmlReport.buildFullUrl(context.getBaseUrl(), resolvedPath));
        logLine(">>> HTTP " + method + " " + resolvedPath
                + " | headers: " + context.getPendingHeaders()
                + " | path: " + context.getPendingPathVars()
                + " | query: " + context.getPendingQueryParams()
                + " | body: " + abbrev(context.getRequestBody(), 800));
        if (dryRun()) {
            context.setSimulatedStatusCode(200);
            simulateDryRunCaptures();
            logLine("<<< (dry-run) no HTTP call executed");
            return;
        }
        var response = client.execute(
                method,
                context.getBaseUrl(),
                resolvedPath,
                context.getRequestBody(),
                context.getPendingPathVars(),
                context.getPendingQueryParams(),
                context.getPendingHeaders()
        );
        context.setResponse(response);
        context.getStore().rememberResponseBody(response.asString());
        logLine("<<< status " + response.statusCode() + " | body: " + abbrev(response.asString(), 1200));
        attachText("response-body.txt", response.asString());
    }

    /**
     * Dry-run hook: the renderer emits {@code capture} steps after the {@code When}, but those
     * fire only after this method returns. Without a stub, the next step's {@code ${var}}
     * references would render empty in the HTML report. Inspecting the Gherkin tags for
     * {@code @flow_id_*} keeps this honest — only flow scenarios get stubs.
     */
    private void simulateDryRunCaptures() {
        // No-op: each {@code capture} step now writes its own stub (see {@link #captureFromPreviousResponse}).
        // Kept as a hook for future "look-ahead" behaviour if the renderer ever needs eager values.
    }

    @Then("response status should be {int}")
    public void responseStatusShouldBe(int statusCode) {
        context.setExpectedHttpStatus(statusCode);
        if (dryRun()) {
            context.setSimulatedStatusCode(statusCode);
            logLine("(dry-run) treating status as " + statusCode);
            return;
        }
        Assertions.assertNotNull(context.getResponse(), "Response should not be null");
        Assertions.assertEquals(statusCode, context.getResponse().statusCode());
    }

    @Then("response body as text contains {string}")
    public void responseBodyContains(String fragment) {
        context.setExpectedBodyFragment(fragment);
        if (dryRun()) {
            logLine("(dry-run) skipping body contains check for: " + fragment);
            return;
        }
        Assertions.assertNotNull(context.getResponse());
        Assertions.assertTrue(
                context.getResponse().asString().contains(fragment),
                "Body should contain: " + fragment
        );
    }

    @And("capture {string} from previous response as {string}")
    public void captureFromPreviousResponse(String jsonPathExpr, String variableRef) {
        String variable = ScenarioContextStore.unwrapVariableReference(variableRef);
        if (dryRun()) {
            String stub = context.getStore().captureDryRunStub(variable, ScenarioContextStore.Scope.FLOW);
            logLine("(dry-run) captured " + jsonPathExpr + " -> ${" + variable + "} = " + stub);
            return;
        }
        Assertions.assertNotNull(
                context.getResponse(),
                "Cannot capture " + jsonPathExpr + " — no previous response available."
        );
        var captured = context.getStore().captureFromLastResponse(
                jsonPathExpr, variable, ScenarioContextStore.Scope.FLOW
        );
        Assertions.assertTrue(
                captured.isPresent(),
                "JSON path " + jsonPathExpr + " did not resolve in previous response."
        );
        logLine("captured " + jsonPathExpr + " -> ${" + variable + "} = " + captured.get());
    }

    @And("response JSON path {string} should equal {string}")
    public void responseJsonPathShouldEqual(String jsonPathExpr, String expectedTemplate) {
        String expected = context.getStore().substitute(expectedTemplate);
        if (dryRun()) {
            logLine("(dry-run) skipping JSON-path equality check: " + jsonPathExpr + " == " + expected);
            return;
        }
        Assertions.assertNotNull(context.getResponse(), "Response should not be null");
        String body = context.getResponse().asString();
        String gpath = ScenarioContextStore.toGPath(jsonPathExpr);
        Object actual = gpath.isEmpty() ? JsonPath.from(body).get() : JsonPath.from(body).get(gpath);
        Assertions.assertEquals(
                expected,
                actual == null ? null : actual.toString(),
                "JSON path " + jsonPathExpr + " mismatch."
        );
    }

    @And("response JSON path {string} should exist")
    public void responseJsonPathShouldExist(String jsonPathExpr) {
        if (dryRun()) {
            logLine("(dry-run) skipping JSON-path existence check: " + jsonPathExpr);
            return;
        }
        Assertions.assertNotNull(context.getResponse(), "Response should not be null");
        String body = context.getResponse().asString();
        String gpath = ScenarioContextStore.toGPath(jsonPathExpr);
        Object actual = gpath.isEmpty() ? JsonPath.from(body).get() : JsonPath.from(body).get(gpath);
        Assertions.assertNotNull(actual, "JSON path " + jsonPathExpr + " should exist but was null.");
    }

    @And("the endpoint contract is logged for reporting")
    public void logEndpointContract() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Endpoint contract snapshot ===\n");
        sb.append("Method: ").append(nullToEmpty(context.getLastHttpMethod())).append("\n");
        sb.append("URL: ").append(nullToEmpty(context.getLastFullUrl())).append("\n");
        sb.append("Path: ").append(nullToEmpty(context.getLastRequestPath())).append("\n");
        sb.append("Headers: ").append(context.getPendingHeaders()).append("\n");
        sb.append("Path vars: ").append(context.getPendingPathVars()).append("\n");
        sb.append("Query: ").append(context.getPendingQueryParams()).append("\n");
        sb.append("Request body (snapshot): ").append(abbrev(nullToEmpty(context.getLastRequestBodySnapshot()), 4000)).append("\n");
        if (dryRun()) {
            sb.append("(dry-run) simulated status: ").append(context.getSimulatedStatusCode()).append("\n");
        } else if (context.getResponse() != null) {
            sb.append("Response status: ").append(context.getResponse().statusCode()).append("\n");
            sb.append("Response body (preview): ")
                    .append(abbrev(context.getResponse().asString(), 8000))
                    .append("\n");
        }
        logLine(sb.toString());
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private void logLine(String msg) {
        if (scenario != null) {
            scenario.log(msg);
        }
    }

    private void attachText(String name, String text) {
        if (scenario == null || text == null) {
            return;
        }
        String t = text.length() > 64_000 ? text.substring(0, 64_000) + "\n...truncated..." : text;
        scenario.attach(t.getBytes(StandardCharsets.UTF_8), "text/plain", name);
    }

    private static String abbrev(String s, int max) {
        if (s == null) {
            return "";
        }
        String t = s.replace("\r", "");
        if (t.length() <= max) {
            return t;
        }
        return t.substring(0, max) + "\n...truncated (" + t.length() + " chars)...";
    }
}
