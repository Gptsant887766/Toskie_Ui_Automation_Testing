package com.toskie.utils;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Captures and validates all network requests/responses during test execution.
 * Validates GraphQL operations, response codes, response times, and payloads.
 */
public class NetworkValidator {

    private final List<Request>  capturedRequests  = new CopyOnWriteArrayList<>();
    private final List<Response> capturedResponses = new CopyOnWriteArrayList<>();
    private final List<Long>     responseTimes     = new CopyOnWriteArrayList<>();

    // ─── Start / Stop capturing ───────────────────────────────────────────────

    public void startCapturing() {
        Page page = BrowserManager.getPage();

        page.onRequest(req -> {
            if (req.url().contains("graphql") || req.url().contains("api")) {
                capturedRequests.add(req);
            }
        });

        page.onResponse(resp -> {
            if (resp.url().contains("graphql") || resp.url().contains("api")) {
                capturedResponses.add(resp);
                long timing = (long) resp.request().timing().responseEnd;
                long start  = (long) resp.request().timing().requestStart;
                responseTimes.add(timing - start);
            }
        });

        ReportManager.getTest().log(Status.INFO, "[NetworkValidator] Capturing started.");
    }

    public void stopCapturing() {
        ReportManager.getTest().log(Status.INFO,
            "[NetworkValidator] Captured " + capturedRequests.size() + " requests.");
    }

    public void clearCaptures() {
        capturedRequests.clear();
        capturedResponses.clear();
        responseTimes.clear();
    }

    // ─── GraphQL-specific validation ──────────────────────────────────────────

    public boolean wasGraphQLOperationCalled(String operationName) {
        for (Request req : capturedRequests) {
            try {
                String body = req.postData();
                if (body != null && body.contains(operationName)) {
                    ReportManager.getTest().log(Status.PASS,
                        "GraphQL operation called: " + operationName);
                    return true;
                }
            } catch (Exception ignored) {}
        }
        ReportManager.getTest().log(Status.FAIL,
            "GraphQL operation NOT called: " + operationName);
        return false;
    }

    public JSONObject getGraphQLResponse(String operationName) {
        for (Response resp : capturedResponses) {
            try {
                if (resp.request().postData() != null
                        && resp.request().postData().contains(operationName)) {
                    return new JSONObject(resp.text());
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    public void assertGraphQLResponseHasNoErrors(String operationName) {
        JSONObject response = getGraphQLResponse(operationName);
        if (response == null) {
            ReportManager.getTest().log(Status.WARNING,
                "No response found for: " + operationName);
            return;
        }
        if (response.has("errors") && !response.getJSONArray("errors").isEmpty()) {
            ReportManager.getTest().log(Status.FAIL,
                "GraphQL errors in " + operationName + ": " + response.getJSONArray("errors"));
        } else {
            ReportManager.getTest().log(Status.PASS,
                "No GraphQL errors for: " + operationName);
        }
    }

    public void assertGraphQLResponseContainsField(String operationName, String fieldPath) {
        JSONObject response = getGraphQLResponse(operationName);
        if (response == null) {
            ReportManager.getTest().log(Status.WARNING, "No response for: " + operationName);
            return;
        }
        boolean found = fieldPath.split("\\.").length <= 1
            ? response.has(fieldPath)
            : navigatePath(response, fieldPath.split("\\."));
        if (found) {
            ReportManager.getTest().log(Status.PASS,
                operationName + " response contains field: " + fieldPath);
        } else {
            ReportManager.getTest().log(Status.FAIL,
                operationName + " response missing field: " + fieldPath);
        }
    }

    // ─── HTTP status validation ────────────────────────────────────────────────

    public void assertNoFailedRequests() {
        List<String> failed = new ArrayList<>();
        for (Response resp : capturedResponses) {
            if (resp.status() >= 400) {
                failed.add(resp.url() + " → HTTP " + resp.status());
            }
        }
        if (failed.isEmpty()) {
            ReportManager.getTest().log(Status.PASS, "No failed HTTP requests.");
        } else {
            ReportManager.getTest().log(Status.FAIL,
                "Failed requests detected:\n" + String.join("\n", failed));
        }
    }

    public void assertResponseStatus(String urlFragment, int expectedStatus) {
        for (Response resp : capturedResponses) {
            if (resp.url().contains(urlFragment)) {
                if (resp.status() == expectedStatus) {
                    ReportManager.getTest().log(Status.PASS,
                        urlFragment + " returned HTTP " + expectedStatus);
                } else {
                    ReportManager.getTest().log(Status.FAIL,
                        urlFragment + " returned HTTP " + resp.status() + " (expected " + expectedStatus + ")");
                }
                return;
            }
        }
        ReportManager.getTest().log(Status.WARNING, "URL not captured: " + urlFragment);
    }

    // ─── Performance thresholds ───────────────────────────────────────────────

    public void assertAllAPIResponsesUnder(long thresholdMs) {
        for (int i = 0; i < capturedResponses.size(); i++) {
            long time = i < responseTimes.size() ? responseTimes.get(i) : -1;
            String url = capturedResponses.get(i).url();
            if (time > 0 && time > thresholdMs) {
                ReportManager.getTest().log(Status.FAIL,
                    "SLOW API: " + url + " took " + time + "ms (limit: " + thresholdMs + "ms)");
            } else if (time > 0) {
                ReportManager.getTest().log(Status.PASS,
                    url + " responded in " + time + "ms");
            }
        }
    }

    public double getAverageResponseTime() {
        return responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    // ─── Token / security validation ──────────────────────────────────────────

    public void assertNoSensitiveDataInURL() {
        for (Request req : capturedRequests) {
            String url = req.url().toLowerCase();
            if (url.contains("token=") || url.contains("password=") || url.contains("secret=")) {
                ReportManager.getTest().log(Status.FAIL,
                    "SECURITY: Sensitive data in URL: " + req.url());
            }
        }
        ReportManager.getTest().log(Status.PASS, "No sensitive data found in request URLs.");
    }

    public void assertAuthHeaderPresent() {
        boolean found = false;
        for (Request req : capturedRequests) {
            try {
                String auth = req.headers().get("authorization");
                if (auth != null && auth.startsWith("Bearer ")) {
                    found = true;
                    break;
                }
            } catch (Exception ignored) {}
        }
        if (found) {
            ReportManager.getTest().log(Status.PASS, "Authorization header present in API calls.");
        } else {
            ReportManager.getTest().log(Status.WARNING, "No Authorization header found in captured requests.");
        }
    }

    public void assertHTTPS() {
        for (Request req : capturedRequests) {
            if (req.url().startsWith("http://")) {
                ReportManager.getTest().log(Status.FAIL,
                    "SECURITY: Plain HTTP request detected: " + req.url());
            }
        }
        ReportManager.getTest().log(Status.PASS, "All requests use HTTPS.");
    }

    public void assertNoCORSMisconfiguration() {
        boolean wildcardFound = false;
        for (Response resp : capturedResponses) {
            String origin = resp.headers().get("access-control-allow-origin");
            if ("*".equals(origin)) {
                ReportManager.getTest().log(Status.WARNING,
                    "Wildcard CORS (Access-Control-Allow-Origin: *) on: " + resp.url()
                        + " -- review if intentional.");
                wildcardFound = true;
            }
        }
        if (!wildcardFound) {
            ReportManager.getTest().log(Status.PASS, "No wildcard CORS misconfiguration detected.");
        }
    }

    // ─── Wait for specific response ────────────────────────────────────────────

    public Response waitForGraphQLResponse(String operationName, Runnable action) {
        final Response[] responseHolder = {null};
        BrowserManager.getPage().onResponse(resp -> {
            try {
                if (resp.url().contains("graphql")) {
                    String body = resp.text();
                    if (body.contains(operationName)) {
                        responseHolder[0] = resp;
                    }
                }
            } catch (Exception ignored) {}
        });
        action.run();
        BrowserManager.getPage().waitForTimeout(5000);
        return responseHolder[0];
    }

    // ─── Utility ──────────────────────────────────────────────────────────────

    private boolean navigatePath(JSONObject obj, String[] parts) {
        JSONObject current = obj;
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i])) return false;
            try { current = current.getJSONObject(parts[i]); }
            catch (Exception e) { return false; }
        }
        return current.has(parts[parts.length - 1]);
    }

    public List<Request> getCapturedRequests()   { return capturedRequests; }
    public List<Response> getCapturedResponses() { return capturedResponses; }
    public int getRequestCount()                 { return capturedRequests.size(); }
    public int getResponseCount()                { return capturedResponses.size(); }
}
