package com.redhat.akashche.wixgen.dir;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redhat.akashche.wixgen.jaxb.Wix;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.Charset;

import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * User: alexkasko
 * Date: 5/12/16
 */
public class DirectoryGeneratorTest {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void test() throws Exception {
        // emulate externally provided conf file
        String json = GSON.toJson(ImmutableMap.builder()
                .put("appName", "Test Wix Application")
                .put("versionMajor", "0")
                .put("versionMinor", "1")
                .put("versionPatch", "0")
                .put("vendor", "Test Vendor")
                .put("licenseFilePath", "src/test/resources/com/redhat/akashche/wixgen/dir/LICENSE.rtf")
                .put("iconPath", "src/test/resources/com/redhat/akashche/wixgen/dir/test_icon.ico")
                .put("topBannerBmpPath", "src/test/resources/com/redhat/akashche/wixgen/dir/top_banner.bmp")
                .put("greetingsBannerBmpPath", "src/test/resources/com/redhat/akashche/wixgen/dir/greetings_banner.bmp")
                .put("registryKeys", ImmutableList.builder()
                        .add(ImmutableMap.builder()
                                .put("root", "HKCU")
                                .put("key", "Software\\Test Wix Application")
                                .put("values", ImmutableList.builder()
                                        .add(ImmutableMap.builder()
                                                .put("type", "string")
                                                .put("name", "Application Name")
                                                .put("value", "Test Application")
                                                .build())
                                        .add(ImmutableMap.builder()
                                                .put("type", "integer")
                                                .put("name", "Version Minor")
                                                .put("value", "1")
                                                .build())
                                        .build())
                                .build())
                        .build())
                .put("environmentVariables", ImmutableList.builder()
                        .add(ImmutableMap.builder()
                                .put("name", "TEST_WIX_VAR")
                                .put("action", "create")
                                .put("value", "Test Wix Var Contents")
                                .build())
                        .add(ImmutableMap.builder()
                                .put("name", "PATH")
                                .put("action", "set")
                                .put("dirPath", "src/test/resources/com/redhat")
                                .build())
                        .build())
                .build());
        WixConfig conf = GSON.fromJson(json, WixConfig.class);
        Wix wix = new DirectoryGenerator().createFromDir(new File("src"), conf);
        JAXBContext jaxb = JAXBContext.newInstance(Wix.class.getPackage().getName());
        Writer writer = null;
        try {
            OutputStream os = new FileOutputStream("target/test.wxs");
            writer = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(wix, writer);
        } finally {
            closeQuietly(writer);
        }
    }
}
