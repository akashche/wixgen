package com.redhat.akashche.wixgen.jaxb;

import org.apache.commons.io.output.NullOutputStream;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.Charset;

import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * User: alexkasko
 * Date: 5/4/16
 */
public class SampleInstallerTest {

    @Test
    public void test() throws Exception {
        JAXBContext jaxb = JAXBContext.newInstance(SampleInstallerTest.class.getPackage().getName());
        InputStream is = null;
        Writer writer = null;
        try {
            is = SampleInstallerTest.class.getResourceAsStream("SampleInstaller.wxs");
            Reader reader = new InputStreamReader(is, Charset.forName("UTF-8"));
            Wix wix = (Wix) jaxb.createUnmarshaller().unmarshal(reader);
            wix.getProduct().setName(wix.getProduct().getName() + " (JAXB)");
//            OutputStream os = new FileOutputStream("SampleInstaller_JAXB.wxs");
            OutputStream os = new NullOutputStream();
            writer = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(wix, writer);
        } finally {
            closeQuietly(is);
            closeQuietly(writer);
        }
    }
}
