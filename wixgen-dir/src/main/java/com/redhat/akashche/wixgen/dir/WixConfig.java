/*
 * Copyright 2016 akashche@redhat.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.akashche.wixgen.dir;

import java.util.ArrayList;

import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Configuration object for the {@link DirectoryGenerator}.
 * Should be created from JSON file, doesn't support creation through API
 *
 * User: akashche
 */
public class WixConfig {
    private String appName = "SPECIFY_ME";
    private String versionMajor = "0";
    private String versionMinor = "0";
    private String versionPatch = "0";
    private String vendor = "SPECIFY_ME";
    private String vendorDirName = "";
    private String installDirName = "";
    private String productUuid = "SPECIFY_ME";
    private String updateUuid = "SPECIFY_ME";
    private int language = 1033;
    private int codepage = 1252;
    private String licenseFilePath = "SPECIFY_ME";
    private String iconPath = "SPECIFY_ME";
    // 493x58
    private String topBannerBmpPath = "SPECIFY_ME";
    // 493x312
    private String greetingsBannerBmpPath = "SPECIFY_ME";

    private ArrayList<RegistryKey> registryKeys = new ArrayList<RegistryKey>();
    private ArrayList<EnvironmentVariable> environmentVariables = new ArrayList<EnvironmentVariable>();

    public String getIconPath() {
        return defaultString(iconPath);
    }

    public String getAppName() {
        return defaultString(appName);
    }

    public String getVendor() {
        return defaultString(vendor);
    }

    public String getVendorDirName() {
        return isNotEmpty(vendorDirName) ? vendorDirName : vendor;
    }

    public String getInstallDirName() {
        return isNotEmpty(installDirName) ? installDirName : appName;
    }

    public String getProductUuid() {
        return defaultString(productUuid);
    }

    public String getUpgradeUuid() {
        return defaultString(updateUuid);
    }

    public String getVersion() {
        return defaultString(versionMajor) + "." + defaultString(versionMinor) + "." + defaultString(versionPatch);
    }

    public String getLanguage() {
        return Integer.toString(language);
    }

    public String getCodepage() {
        return Integer.toString(codepage);
    }

    public String getLicenseFilePath() {
        return defaultString(licenseFilePath);
    }

    public String getTopBannerBmpPath() {
        return defaultString(topBannerBmpPath);
    }

    public String getGreetingsBannerBmpPath() {
        return defaultString(greetingsBannerBmpPath);
    }

    public ArrayList<RegistryKey> getRegistryKeys() {
        return null != registryKeys ? registryKeys : new ArrayList<RegistryKey>();
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return null != environmentVariables ? environmentVariables : new ArrayList<EnvironmentVariable>();
    }

    static class RegistryKey {
        String root;
        String key;
        ArrayList<RegistryValue> values = new ArrayList<RegistryValue>();

        public String getRoot() {
            return defaultString(root);
        }

        public String getKey() {
            return defaultString(key);
        }
    }

    static class RegistryValue {
        String type = "string";
        String name = "SPECIFY_ME";
        String value = "SPECIFY_ME";

        public String getType() {
            return defaultString(type);
        }

        public String getName() {
            return defaultString(name);
        }

        public String getValue() {
            return defaultString(value);
        }
    }

    static class EnvironmentVariable {
        private String name = "SPECIFY_ME";
        private String action = "create";
        private boolean system = true;
        private String part = "first";
        private String value = "SPECIFY_ME";

        public String getName() {
            return defaultString(name);
        }

        public String getAction() {
            return defaultString(action);
        }

        public String getSystem() {
            return system ? "yes" : "no";
        }

        public String getPart() {
            return defaultString(part);
        }

        public String getValue() {
            return defaultString(value);
        }
    }
}
