package com.toskie.utils_Layer;

import com.toskie.config.ConfigReader;

/**
 * Legacy config facade — now delegates to ConfigReader so that all code
 * (legacy and enterprise) uses the same three-tier priority chain:
 *   1. System property   (-DbaseUrl=…)
 *   2. Environment var   (ENVIRONMENT=qa → loads environments/qa.properties)
 *   3. config.properties (base defaults)
 *
 * No callers need to change — this class preserves the original public API.
 * New code should import ConfigReader directly.
 */
public final class ConfigManager {

    private ConfigManager() {}

    public static String  getBrowser()     { return ConfigReader.getBrowser();    }
    public static String  getBaseUrl()     { return ConfigReader.getBaseUrl();    }
    public static boolean getHeadless()    { return ConfigReader.isHeadless();    }
    public static String  getTestMobile()  { return ConfigReader.getTestMobile(); }
    public static String  getTestEmail()   { return ConfigReader.getTestEmail();  }
    public static String  getApiUrl()      { return ConfigReader.getApiUrl();     }
    public static String  getQaSecret()    { return ConfigReader.getQaSecret();   }
    public static String  getDeviceId()    { return ConfigReader.getDeviceId();   }
    public static String  getFingerprint() { return ConfigReader.getFingerprint();}
    public static String  get(String key)  { return ConfigReader.get(key);        }
}
