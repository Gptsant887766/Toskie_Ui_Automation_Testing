package com.toskie.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Enterprise config reader with three-tier priority:
 *   1. System property (-Dbrowser=chrome)  — CI/CD pipeline flags
 *   2. Environment variable (BROWSER=chrome) — Docker / shell env
 *   3. config.properties                    — checked-in defaults
 *
 * Delegates to the existing ConfigManager for actual property file loading
 * while adding the priority chain and typed accessors.
 */
public final class ConfigReader {

    private ConfigReader() {}

    private static final Properties props = new Properties();

    static {
        // 1. Base defaults (checked-in, environment-agnostic)
        loadFile("config.properties");

        // 2. Environment-specific overlay — values here override the base.
        //    Priority: -Denvironment=qa  →  env var ENVIRONMENT=qa  →  skip
        //    Files live in: src/test/resources/environments/{env}.properties
        String env = System.getProperty("environment");
        if (env == null || env.isBlank()) {
            env = System.getenv("ENVIRONMENT");
        }
        if (env != null && !env.isBlank()) {
            loadFile("environments/" + env.toLowerCase().trim() + ".properties");
        }
    }

    private static void loadFile(String filename) {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(filename)) {
            if (is != null) {
                props.load(is);
            } else {
                System.err.println("[ConfigReader] Not found on classpath: " + filename);
            }
        } catch (Exception e) {
            System.err.println("[ConfigReader] Failed to load " + filename + ": " + e.getMessage());
        }
    }

    // ─── Core resolution ──────────────────────────────────────────────────────

    /**
     * Resolves a key with priority: System property → env var → config.properties → defaultValue.
     * Env var key is derived by uppercasing and replacing "." / "-" with "_"
     * (e.g. "base.url" → "BASE_URL").
     */
    public static String get(String key, String defaultValue) {
        // 1. System property (e.g. mvn test -Dbase.url=...)
        String val = System.getProperty(key);
        if (val != null && !val.isBlank()) return val.trim();

        // 2. Environment variable (e.g. BASE_URL=...)
        String envKey = key.replace(".", "_").replace("-", "_").toUpperCase();
        val = System.getenv(envKey);
        if (val != null && !val.isBlank()) return val.trim();

        // 3. config.properties
        val = props.getProperty(key);
        if (val != null && !val.isBlank()) return val.trim();

        return defaultValue;
    }

    public static String get(String key) {
        return get(key, null);
    }

    // ─── Typed accessors ─────────────────────────────────────────────────────

    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key);
        if (val == null) return defaultValue;
        return "true".equalsIgnoreCase(val) || "yes".equalsIgnoreCase(val) || "1".equals(val);
    }

    public static int getInt(String key, int defaultValue) {
        try {
            String val = get(key);
            return val != null ? Integer.parseInt(val) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long getLong(String key, long defaultValue) {
        try {
            String val = get(key);
            return val != null ? Long.parseLong(val) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // ─── Named convenience getters ────────────────────────────────────────────

    public static String  getBrowser()     { return get("browser",     "chrome"); }
    public static String  getBaseUrl()     { return get("baseUrl",     "");       }
    public static boolean isHeadless()     { return getBoolean("headless", false); }
    public static String  getTestMobile()  { return get("testMobile",  "");       }
    public static String  getTestEmail()   { return get("testEmail",   "");       }
    public static String  getApiUrl()      { return get("apiUrl",      "");       }
    public static String  getQaSecret()    { return get("qaSecret",    "");       }
    public static String  getEnvironment() { return get("environment", "dev");    }
    public static int     getMaxRetries()  { return getInt("max.retries", 3);     }
    public static int     getSlowMo()      { return getInt("slowMo",      0);     }
    public static String  getDeviceId()    { return get("deviceId",    "automation-device"); }
    public static String  getFingerprint() { return get("fingerprint", "automation-fp");     }
}
