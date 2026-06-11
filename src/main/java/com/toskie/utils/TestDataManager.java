package com.toskie.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Loads test data from JSON files in src/test/resources/testdata/.
 * Supports @DataProvider-ready Object[][] conversion.
 */
public class TestDataManager {

    private static final String DATA_ROOT = "testdata/";

    // ─── Load JSON file ───────────────────────────────────────────────────────

    public static JSONArray loadJsonArray(String fileName) {
        try (InputStream is = TestDataManager.class.getClassLoader()
                .getResourceAsStream(DATA_ROOT + fileName)) {
            if (is == null) throw new RuntimeException("Test data file not found: " + DATA_ROOT + fileName);
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONArray(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data: " + fileName, e);
        }
    }

    public static JSONObject loadJsonObject(String fileName) {
        try (InputStream is = TestDataManager.class.getClassLoader()
                .getResourceAsStream(DATA_ROOT + fileName)) {
            if (is == null) throw new RuntimeException("Test data file not found: " + DATA_ROOT + fileName);
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONObject(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data: " + fileName, e);
        }
    }

    // ─── Convert to Object[][] for @DataProvider ──────────────────────────────

    public static Object[][] toDataProviderArray(String fileName, String... keys) {
        JSONArray arr = loadJsonArray(fileName);
        Object[][] data = new Object[arr.length()][keys.length];
        for (int i = 0; i < arr.length(); i++) {
            JSONObject row = arr.getJSONObject(i);
            for (int j = 0; j < keys.length; j++) {
                data[i][j] = row.optString(keys[j], "");
            }
        }
        return data;
    }

    // ─── Specific data loaders ────────────────────────────────────────────────

    public static Object[][] getLoginData() {
        return toDataProviderArray("login-data.json", "mobile", "expectedResult", "testType");
    }

    public static Object[][] getProfileData() {
        return toDataProviderArray("profile-data.json", "firstName", "lastName", "email", "gender", "dob");
    }

    public static Object[][] getNegativeLoginData() {
        return toDataProviderArray("negative-data.json", "input", "field", "expectedError");
    }

    public static Object[][] getSearchData() {
        return toDataProviderArray("search-data.json", "query", "expectedMinResults", "expectedCategory");
    }

    public static Object[][] getEdgeCaseData() {
        return toDataProviderArray("edge-case-data.json", "field", "input", "expectedBehavior");
    }

    public static Object[][] getSecurityPayloads() {
        // File is a JSON object with a "payloads" array, not a top-level array
        JSONArray arr = loadJsonObject("security-payloads.json").getJSONArray("payloads");
        Object[][] data = new Object[arr.length()][3];
        for (int i = 0; i < arr.length(); i++) {
            JSONObject row = arr.getJSONObject(i);
            data[i][0] = row.optString("payload", "");
            data[i][1] = row.optString("field", "");
            data[i][2] = row.optString("expectedResult", "");
        }
        return data;
    }

    // ─── Single value helpers ─────────────────────────────────────────────────

    public static String getTestMobile() {
        try {
            JSONArray arr = loadJsonArray("login-data.json");
            return arr.getJSONObject(0).getString("mobile");
        } catch (Exception e) {
            return "9919011050";
        }
    }

    public static String getTestEmail() {
        try {
            JSONArray arr = loadJsonArray("profile-data.json");
            return arr.getJSONObject(0).getString("email");
        } catch (Exception e) {
            return "gptsant@gmail.com";
        }
    }

    public static List<String> getXSSPayloads() {
        List<String> payloads = new ArrayList<>();
        try {
            JSONObject obj = loadJsonObject("security-payloads.json");
            JSONArray xss = obj.getJSONArray("xss");
            for (int i = 0; i < xss.length(); i++) {
                payloads.add(xss.getString(i));
            }
        } catch (Exception e) {
            payloads.addAll(Arrays.asList(
                "<script>alert('XSS')</script>",
                "<img src=x onerror=alert(1)>",
                "javascript:alert(1)",
                "<svg onload=alert(1)>",
                "'\"><script>alert(document.cookie)</script>"
            ));
        }
        return payloads;
    }

    public static List<String> getSQLInjectionPayloads() {
        return Arrays.asList(
            "' OR '1'='1",
            "'; DROP TABLE users; --",
            "' UNION SELECT * FROM users --",
            "1' AND '1'='1",
            "admin'--",
            "' OR 1=1--",
            "'; EXEC xp_cmdshell('dir'); --"
        );
    }

    public static Map<String, String> getValidProfileData() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("firstName", "Sontosh");
        data.put("lastName",  "Gupta");
        data.put("email",     "gptsant@gmail.com");
        data.put("gender",    "MALE");
        data.put("mobile",    "9919011050");
        return data;
    }
}
