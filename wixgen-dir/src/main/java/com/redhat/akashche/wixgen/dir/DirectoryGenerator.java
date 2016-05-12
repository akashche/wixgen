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

    Wix createFromDir(File dir) throws IOException {
        List<ComponentRef> comprefs = new ArrayList<ComponentRef>();
        Collection<Object> contents = createContents(dir, comprefs);
        return new Wix()
                .withProduct(new Product()
                        .withName("Test Name")
                        .withManufacturer("Test Vendor")
                        .withId(UUID.randomUUID().toString())
                        .withUpgradeCode(UUID.randomUUID().toString())
                        .withLanguage("1033")
                        .withCodepage("1252")
                        .withVersion("1.0")
                        .withPackage(new Package()
                                .withKeywords("Test Keywords")
                                .withDescription("Test Description")
                                .withComments("Test Comments")
                                .withInstallerVersion(new BigInteger("200"))
                                .withLanguages("1033")
                                .withCompressed("yes")
                                .withSummaryCodepage("1252")
                                .withPlatform("x64"))
                        .withAppIdOrBinaryOrComplianceCheck(new Media()
                                .withId("1")
                                .withCabinet("Sample.cab")
                                .withEmbedCab("yes")
                                .withDiskPrompt("CD-ROM #1"))
                        .withAppIdOrBinaryOrComplianceCheck(new Property()
                                .withId("DiskPrompt")
                                .withValue("Test Property"))
                        .withAppIdOrBinaryOrComplianceCheck(new Directory()
                                .withId("TARGETDIR")
                                .withName("SourceDir")
                                .withComponentOrDirectoryOrMerge(new Directory()
                                        .withId("ProgramFiles64Folder")
                                        .withComponentOrDirectoryOrMerge(new Directory()
                                                .withId("INSTALLDIR")
                                                .withName("TestApp")
                                                .withComponentOrDirectoryOrMerge((Collection)contents))))
                        .withAppIdOrBinaryOrComplianceCheck(new Feature()
                                .withId(genId())
                                .withTitle("Test Feature Title")
                                .withDescription("Test Feature Description")
                                .withDisplay("expand")
                                .withLevel(new BigInteger("1"))
                                .withConfigurableDirectory("INSTALLDIR")
                                .withComponentOrComponentGroupRefOrComponentRef((Collection)comprefs))
                        .withAppIdOrBinaryOrComplianceCheck(new Property()
                                .withId("WIXUI_INSTALLDIR")
                                .withValue("INSTALLDIR"))
                        .withAppIdOrBinaryOrComplianceCheck(new UIRef()
                                .withId("WixUI_InstallDir"))
                        .withAppIdOrBinaryOrComplianceCheck(new UIRef()
                                .withId("WixUI_ErrorProgressText")));
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
}
