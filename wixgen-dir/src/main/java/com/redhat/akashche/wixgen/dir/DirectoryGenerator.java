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

import com.redhat.akashche.wixgen.jaxb.*;
import com.redhat.akashche.wixgen.jaxb.Package;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Allows to generate Wix JAXB objects from input directories
 *
 * User: akashche
 */
public class DirectoryGenerator {

    /**
     * Generates WiX installer descriptor as a JAXB object
     *
     * @param dir input directory to be used as installation contents
     * @param conf configuration object
     * @return  WiX installer descriptor as a JAXB object
     * @throws IOException on input dir, config file or output dir access error
     */
    @SuppressWarnings("unchecked") // varargs fluent setter
    public Wix createFromDir(File dir, WixConfig conf) throws IOException {
        List<ComponentRef> comprefs = new ArrayList<ComponentRef>();
        Collection<Object> files = createContents(conf, dir, comprefs);
        Collection<Object> icon = createIcon(conf);
        Collection<Object> regs = createRegs(conf, comprefs);
        Collection<Object> envs = createEnvs(conf, comprefs);
        return new Wix()
                .withProduct(new Product()
                        .withName(conf.getAppName())
                        .withManufacturer(conf.getVendor())
                        .withId(conf.isUpdateEnabled() ? UUID.randomUUID().toString() : conf.getProductUuid())
                        .withUpgradeCode(conf.getUpgradeUuid())
                        .withLanguage(conf.getLanguage())
                        .withCodepage(conf.getCodepage())
                        .withVersion(conf.getVersion())
                        .withPackage(new Package()
                                .withInstallerVersion(new BigInteger("200"))
                                .withLanguages(conf.getLanguage())
                                .withCompressed("yes")
                                .withSummaryCodepage(conf.getCodepage())
                                .withPlatform(conf.isWin64() ? "x64" : "x86"))
                        .withAppIdOrBinaryOrComplianceCheck(new Media()
                                .withId("1")
                                .withCabinet("Application.cab")
                                .withEmbedCab("yes"))
                        .withAppIdOrBinaryOrComplianceCheck(new Directory()
                                .withId("TARGETDIR")
                                .withName("SourceDir")
                                .withComponentOrDirectoryOrMerge(new Directory()
                                        .withId(conf.isWin64() ? "ProgramFiles64Folder" : "ProgramFilesFolder")
                                        .withComponentOrDirectoryOrMerge(new Directory()
                                                .withId(genId())
                                                .withName(conf.getVendorDirName())
                                                .withComponentOrDirectoryOrMerge(new Directory()
                                                        .withId(conf.getFeatureDirectoryId())
                                                        .withName(conf.getInstallDirName())
                                                        .withComponentOrDirectoryOrMerge((Collection) files)
                                                        .withComponentOrDirectoryOrMerge((Collection) regs)
                                                        .withComponentOrDirectoryOrMerge((Collection) envs)))))
                        .withAppIdOrBinaryOrComplianceCheck(createFeature(conf, comprefs))
                        .withAppIdOrBinaryOrComplianceCheck(new Property()
                                .withId("WIXUI_INSTALLDIR")
                                .withValue(conf.getFeatureDirectoryId()))
                        .withAppIdOrBinaryOrComplianceCheck(new UIRef()
                                .withId(conf.isUseFeatureTree() ? "WixUI_FeatureTree" : "WixUI_InstallDir"))
                        .withAppIdOrBinaryOrComplianceCheck(new UIRef()
                                .withId("WixUI_ErrorProgressText"))
                        .withAppIdOrBinaryOrComplianceCheck((Collection) icon)
                        .withAppIdOrBinaryOrComplianceCheck(new WixVariable()
                                .withId("WixUILicenseRtf")
                                .withValue(conf.getLicenseFilePath()))
                        .withAppIdOrBinaryOrComplianceCheck(new WixVariable()
                                .withId("WixUIBannerBmp")
                                .withValue(conf.getTopBannerBmpPath()))
                        .withAppIdOrBinaryOrComplianceCheck(new WixVariable()
                                .withId("WixUIDialogBmp")
                                .withValue(conf.getGreetingsBannerBmpPath()))
                        .withAppIdOrBinaryOrComplianceCheck(new Property()
                                .withId("ARPHELPLINK")
                                .withValue(conf.getHelpLink()))
                        .withAppIdOrBinaryOrComplianceCheck(new MajorUpgrade()
                                .withAllowDowngrades(conf.isUpdateAllowDowngrades() ? "yes" : "no")
                                .withAllowSameVersionUpgrades(conf.isUpdateAllowSameVersionUpgrades() ? "yes" : "no")
                                .withDowngradeErrorMessage(conf.getUpdateDowngradeErrorMessage())
                                .withIgnoreRemoveFailure(conf.isUpdateIgnoreRemoveFailure() ? "yes" : "no"))
                );
    }

    /**
     * Extracts the root directory element from an installer descriptor
     *
     * @param wix WiX installer descriptor as a JAXB object
     * @return root directory element
     */
    public static Directory findWixDirectory(Wix wix) {
        for (Object obj : wix.getProduct().getAppIdOrBinaryOrComplianceCheck()) {
            if (obj instanceof Directory) {
                Directory target = (Directory) obj;
                Directory pf = (Directory) target.getComponentOrDirectoryOrMerge().get(0);
                Directory vendor = (Directory) pf.getComponentOrDirectoryOrMerge().get(0);
                return (Directory) vendor.getComponentOrDirectoryOrMerge().get(0);
            }
        }
        throw new RuntimeException("Cannot find feature directory");
    }

    /**
     * Extracts the feature element from an installer descriptor
     *
     * @param wix WiX installer descriptor as a JAXB object
     * @return feature element
     */
    public static Feature findWixFeature(Wix wix) {
        for (Object obj : wix.getProduct().getAppIdOrBinaryOrComplianceCheck()) {
            if (obj instanceof Feature) {
                return (Feature) obj;
            }
        }
        throw new RuntimeException("Cannot find feature");
    }

    private static String genId() {
        return "_" + UUID.randomUUID().toString().replace('-', '_');
    }

    private List<Object> createContents(WixConfig conf, File dir, List<ComponentRef> comprefs) throws IOException {
        List<Object> contents = new ArrayList<Object>();
        if (!dir.isDirectory()) throw new IOException("Invalid directory: [" + dir.getAbsolutePath() + "]");
        File[] listing = dir.listFiles();
        if (null == listing) throw new IOException("Cannot list directory: [" + dir.getAbsolutePath() + "]");
        for (File fi : listing) {
            if (fi.isDirectory()) {
                contents.add(createDirRecursive(conf, fi, comprefs));
            } else {
                contents.add(createComp(conf, fi, comprefs));
            }
        }
        return contents;
    }

    private static Directory createDirRecursive(WixConfig conf, File dir, List<ComponentRef> comprefs) throws IOException {
        if (!dir.isDirectory()) throw new IOException("Invalid directory: [" + dir.getAbsolutePath() + "]");
        Directory res = new Directory()
                .withId(genId())
                .withName(dir.getName());
        File[] listing = dir.listFiles();
        if (null == listing) throw new IOException("Cannot list directory: [" + dir.getAbsolutePath() + "]");
        for (File fi : listing) {
            if (fi.isDirectory()) {
                res.withComponentOrDirectoryOrMerge(createDirRecursive(conf, fi, comprefs));
            } else {
                res.withComponentOrDirectoryOrMerge(createComp(conf, fi, comprefs));
            }
        }
        return res;
    }

    private static Component createComp(WixConfig conf, File file, List<ComponentRef> comprefs) throws IOException {
        if (!file.isFile()) throw new IOException("Invalid file: [" + file.getAbsolutePath() + "]");
        String id = genId();
        comprefs.add(new ComponentRef()
                .withId(id));
        return new Component()
                .withId(id)
                .withWin64(conf.isWin64() ? "yes" : "no")
                .withGuid(UUID.randomUUID().toString())
                .withAppIdOrCategoryOrClazz(new com.redhat.akashche.wixgen.jaxb.File()
                        .withId(genId())
                        .withName(file.getName())
                        .withDiskId("1")
                        .withSource(file.getPath())
                        .withKeyPath("yes"));
    }

    @SuppressWarnings("unchecked") // varargs fluent setter
    private static Feature createFeature(WixConfig conf, List<ComponentRef> comprefs) {
        Feature feature = new Feature()
                .withId(conf.getFeatureId())
                .withAbsent(conf.isFeatureOptional() ? "allow" : "disallow")
                .withLevel(BigInteger.valueOf(conf.getFeatureLevel()))
                .withAllowAdvertise("no")
                .withTitle(conf.getFeatureTitle())
                .withDescription(conf.getFeatureDescription())
                .withComponentOrComponentGroupRefOrComponentRef((Collection) comprefs);
        if (conf.isFeatureConfigurableDirectory()) {
            feature.setConfigurableDirectory(conf.getFeatureDirectoryId());
        }
        return feature;
    }

    private static Collection<Object> createIcon(WixConfig conf) {
        String id = genId();
        return Arrays.asList(
                new Icon()
                        .withId(id)
                        .withSourceFile(conf.getIconPath()),
                new Property()
                        .withId("ARPPRODUCTICON")
                        .withValue(id)
        );
    }

    @SuppressWarnings("unchecked") // varargs fluent setter
    private static Collection<Object> createRegs(WixConfig conf, List<ComponentRef> comprefs) {
        List<Object> res = new ArrayList<Object>();
        for (WixConfig.RegistryKey rk : conf.getRegistryKeys()) {
            String id = genId();
            comprefs.add(new ComponentRef()
                    .withId(id));
            Collection<Object> values = new ArrayList<Object>();
            for (WixConfig.RegistryValue rv : rk.values) {
                RegistryValue val = new RegistryValue()
                        .withType(rv.getType())
                        .withValue(rv.getValue());
                if (!rv.getName().isEmpty()) {
                    val.setName(rv.getName());
                }
                values.add(val);
            }
            res.add(new Component()
                    .withId(id)
                    .withWin64(conf.isWin64() ? "yes" : "no")
                    .withGuid(UUID.randomUUID().toString())
                    .withAppIdOrCategoryOrClazz(new RegistryKey()
                            .withId(genId())
                            .withForceCreateOnInstall("yes")
                            .withRoot(RegistryRootType.fromValue(rk.getRoot()))
                            .withKey(rk.getKey())
                            .withRegistryKeyOrRegistryValueOrPermission((Collection) values))
            );
        }
        return res;
    }

    private static List<Object> createEnvs(WixConfig conf, List<ComponentRef> comprefs) {
        List<Object> res = new ArrayList<Object>();
        for (WixConfig.EnvironmentVariable ev : conf.getEnvironmentVariables()) {
            String id = genId();
            comprefs.add(new ComponentRef()
                    .withId(id));
            res.add(new Component()
                    .withId(id)
                    .withWin64(conf.isWin64() ? "yes" : "no")
                    .withGuid(UUID.randomUUID().toString())
                    .withKeyPath("yes")
                    .withAppIdOrCategoryOrClazz(new Environment()
                            .withId(genId())
                            .withName(ev.getName())
                            .withAction(ev.getAction())
                            .withSystem(ev.getSystem())
                            .withPart("set".equals(ev.getAction()) ? ev.getPart() : null)
                            .withValue(ev.getValue())));
        }
        return res;
    }
}
