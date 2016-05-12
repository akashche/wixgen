package com.redhat.akashche.wixgen.dir;

import static org.apache.commons.lang.StringUtils.defaultString;

/**
 * User: alexkasko
 * Date: 5/12/16
 */
public class WixConfig {
    private String appName = "Test Wix Application";
    private int versionMajor = 0;
    private int versionMinor = 1;
    private int versionPatch = 0;
    private String vendor = "Test Wix Vendor";
    private int language = 1033;
    private int codepage = 1252;
    private String licenseFilePath = "com/redhat/akashche/wixgen/dir/LICENSE.rtf";
    // TODO: fixme
    private String iconPath = "com/redhat/akashche/wixgen/dir/test_icon.ico";
    private String topBannerPath = "com/redhat/akashche/wixgen/dir/top_banner.bmp";
    private String greetingsBannerPath = "com/redhat/akashche/wixgen/dir/greetings_banner.bmp";

    public String getIconPath() {
        return defaultString(iconPath);
    }

    public String getAppName() {
        return defaultString(appName);
    }

    public String getVendor() {
        return defaultString(vendor);
    }

    public String getVersion() {
        return Integer.toString(versionMajor) + "." + Integer.toString(versionMinor) + "." + Integer.toString(versionPatch);
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

    public String getTopBannerPath() {
        return defaultString(topBannerPath);
    }

    public String getGreetingsBannerPath() {
        return defaultString(greetingsBannerPath);
    }
}
