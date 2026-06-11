package com.toskie.utils_Layer;

import java.io.InputStream;
import java.util.Properties;

/**
 * Loads all configuration from config.properties.
 * System properties always override file values (so -Dbrowser=firefox works).
 */
public class ConfigManager {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                System.err.println("[ConfigManager] config.properties not found on classpath.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String get(String key) {
        return System.getProperty(key, props.getProperty(key));
    }

    public static String getBrowser()     { return get("browser"); }
    public static String getBaseUrl()     { return get("baseUrl"); }
    public static boolean getHeadless()   { return Boolean.parseBoolean(get("headless")); }
    public static String getTestMobile()  { return get("testMobile"); }
    public static String getTestEmail()   { return get("testEmail"); }
    public static String getApiUrl()      { return get("apiUrl"); }
    public static String getQaSecret()    { return get("qaSecret"); }
    public static String getDeviceId()    { return get("deviceId"); }
    public static String getFingerprint() { return get("fingerprint"); }
}
