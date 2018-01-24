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
import java.util.UUID;

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
    private int versionMajor = 0;
    private int versionMinor = 0;
    private int versionMicro = 0;
    private int versionPatch = 0;
    private String vendor = "SPECIFY_ME";
    private String vendorDirName = "";
    private String installDirName = "";
    private String helpLink = "";
    private String productUuid = "SPECIFY_ME";

    private boolean useFeatureTree = false;
    private String featureId = "_" + UUID.randomUUID().toString().replace('-', '_');
    private String featureTitle = "SPECIFY_ME";
    private String featureDescription = "SPECIFY_ME";

    private String updateUuid = "SPECIFY_ME";
    private boolean updateEnabled = true;
    private boolean updateAllowDowngrades = false;
    private boolean updateAllowSameVersionUpgrades = true;
    private String updateDowngradeErrorMessage = "A later version of [ProductName] is already installed. Setup will now exit.";
    private boolean updateIgnoreRemoveFailure = false;

    private boolean win64 = true;
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

    public String getHelpLink() {
        return defaultString(helpLink);
    }

    public String getProductUuid() {
        return defaultString(productUuid);
    }

    public boolean isUseFeatureTree() {
        return useFeatureTree;
    }

    public String getFeatureId() {
        return defaultString(featureId);
    }

    public String getFeatureTitle() {
        return defaultString(featureTitle);
    }

    public String getFeatureDescription() {
        return defaultString(featureDescription);
    }

    public String getUpgradeUuid() {
        return defaultString(updateUuid);
    }

    public boolean isUpdateEnabled() {
        return updateEnabled;
    }

    public boolean isUpdateAllowDowngrades() {
        return updateAllowDowngrades;
    }

    public boolean isUpdateAllowSameVersionUpgrades() {
        return updateAllowSameVersionUpgrades;
    }

    public String getUpdateDowngradeErrorMessage() {
        return updateDowngradeErrorMessage;
    }

    public boolean isUpdateIgnoreRemoveFailure() {
        return updateIgnoreRemoveFailure;
    }

    public String getVersion() {
        return new StringBuilder()
                .append(versionMajor)
                .append(".")
                .append(versionMinor)
                .append(".")
                .append(versionMicro)
                .append(".")
                .append(versionPatch)
                .toString();
    }

    public boolean isWin64() {
        return win64;
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
        String name = "";
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
