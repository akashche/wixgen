package com.redhat.akashche.wixgen.dir;

import com.redhat.akashche.wixgen.jaxb.*;
import com.redhat.akashche.wixgen.jaxb.Package;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * User: alexkasko
 * Date: 5/11/16
 */
public class DirectoryGenerator {

    @SuppressWarnings("unchecked") // varargs fluent setter
    Wix createFromDir(File dir, WixConfig conf) throws IOException {
        List<ComponentRef> comprefs = new ArrayList<ComponentRef>();
        Collection<Object> files = createContents(dir, comprefs);
        Collection<Object> icon = createIcon(conf);
        Collection<Object> regs = createRegs(conf, comprefs);
        return new Wix()
                .withProduct(new Product()
                        .withName(conf.getAppName())
                        .withManufacturer(conf.getVendor())
                        .withId(UUID.randomUUID().toString())
                        .withUpgradeCode(UUID.randomUUID().toString())
                        .withLanguage(conf.getLanguage())
                        .withCodepage(conf.getCodepage())
                        .withVersion(conf.getVersion())
                        .withPackage(new Package()
                                .withInstallerVersion(new BigInteger("200"))
                                .withLanguages(conf.getLanguage())
                                .withCompressed("yes")
                                .withSummaryCodepage(conf.getCodepage())
                                .withPlatform("x64"))
                        .withAppIdOrBinaryOrComplianceCheck(new Media()
                                .withId("1")
                                .withCabinet("Application.cab")
                                .withEmbedCab("yes")
                        )
                        .withAppIdOrBinaryOrComplianceCheck(new Directory()
                                .withId("TARGETDIR")
                                .withName("SourceDir")
                                .withComponentOrDirectoryOrMerge(new Directory()
                                        .withId("ProgramFiles64Folder")
                                        .withComponentOrDirectoryOrMerge(new Directory()
                                                .withId("INSTALLDIR")
                                                .withName(conf.getAppName())
                                                .withComponentOrDirectoryOrMerge((Collection) files)
                                                .withComponentOrDirectoryOrMerge((Collection) regs))))
                        .withAppIdOrBinaryOrComplianceCheck(new Feature()
                                .withId(genId())
                                .withConfigurableDirectory("INSTALLDIR")
                                .withComponentOrComponentGroupRefOrComponentRef((Collection) comprefs))
                        .withAppIdOrBinaryOrComplianceCheck(new Property()
                                .withId("WIXUI_INSTALLDIR")
                                .withValue("INSTALLDIR"))
                        .withAppIdOrBinaryOrComplianceCheck(new UIRef()
                                .withId("WixUI_InstallDir"))
                        .withAppIdOrBinaryOrComplianceCheck(new UIRef()
                                .withId("WixUI_ErrorProgressText"))
                        .withAppIdOrBinaryOrComplianceCheck((Collection)icon)
                        .withAppIdOrBinaryOrComplianceCheck(new WixVariable()
                                .withId("WixUILicenseRtf")
                                .withValue(conf.getLicenseFilePath()))
                        .withAppIdOrBinaryOrComplianceCheck(new WixVariable()
                                .withId("WixUIBannerBmp")
                                .withValue(conf.getTopBannerBmpPath()))
                        .withAppIdOrBinaryOrComplianceCheck(new WixVariable()
                                .withId("WixUIDialogBmp")
                                .withValue(conf.getGreetingsBannerBmpPath()))
                );
    }

    private String genId() {
        return "_" + UUID.randomUUID().toString().replace('-', '_');
    }

    private List<Object> createContents(File dir, List<ComponentRef> comprefs) throws IOException {
        List<Object> contents = new ArrayList<Object>();
        if (!dir.isDirectory()) throw new IOException("Invalid directory: [" + dir.getAbsolutePath() + "]");
        File[] listing = dir.listFiles();
        if (null == listing) throw new IOException("Cannot list directory: [" + dir.getAbsolutePath() + "]");
        for (File fi : listing) {
            if (fi.isDirectory()) {
                contents.add(createDirRecursive(fi, comprefs));
            } else {
                contents.add(createComp(fi, comprefs));
            }
        }
        return contents;
    }

    private Directory createDirRecursive(File dir, List<ComponentRef> comprefs) throws IOException {
        if (!dir.isDirectory()) throw new IOException("Invalid directory: [" + dir.getAbsolutePath() + "]");
        Directory res = new Directory()
                .withId(genId())
                .withName(dir.getName());
        File[] listing = dir.listFiles();
        if (null == listing) throw new IOException("Cannot list directory: [" + dir.getAbsolutePath() + "]");
        for (File fi : listing) {
            if (fi.isDirectory()) {
                res.withComponentOrDirectoryOrMerge(createDirRecursive(fi, comprefs));
            } else {
                res.withComponentOrDirectoryOrMerge(createComp(fi, comprefs));
            }
        }
        return res;
    }

    private Component createComp(File file, List<ComponentRef> comprefs) throws IOException {
        if (!file.isFile()) throw new IOException("Invalid file: [" + file.getAbsolutePath() + "]");
        String id = genId();
        comprefs.add(new ComponentRef()
                .withId(id));
        return new Component()
                .withId(id)
                .withWin64("yes")
                .withGuid(UUID.randomUUID().toString())
                .withAppIdOrCategoryOrClazz(new com.redhat.akashche.wixgen.jaxb.File()
                        .withId(genId())
                        .withName(file.getName())
                        .withDiskId("1")
                        .withSource(file.getPath())
                        .withKeyPath("yes"));
    }

    private Collection<Object> createIcon(WixConfig conf) {
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
    private Collection<Object> createRegs(WixConfig conf, List<ComponentRef> comprefs) {
        List<Object> res = new ArrayList<Object>();
        for (WixConfig.RegistryKey rk : conf.getRegistryKeys()) {
            String id = genId();
            comprefs.add(new ComponentRef()
                    .withId(id));
            Collection<Object> values = new ArrayList<Object>();
            for (WixConfig.RegistryValue rv : rk.values) {
                values.add(new RegistryValue()
                        .withType(rv.getType())
                        .withName(rv.getName())
                        .withValue(rv.getValue()));
            }
            res.add(new Component()
                    .withId(id)
                    .withWin64("yes")
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
}
